package com.magnet.demo.mmx.rpsls;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
  private static final String TAG = MainActivity.class.getSimpleName();
  static final int REQUEST_LOGIN = 1;

  private MyProfile mProfile = null;
  private Handler mMainHandler = null;
  private Button mFindOpponentButton = null;
  private ViewAnimator mViewAnimator = null;
  private TextView mUsernameView = null;
  private TextView mWinsView = null;
  private TextView mLossesView = null;
  private TextView mDrawsView = null;
  private ListView mAvailablePlayersListView = null;
  private UserProfileAdapter mAvailablePlayersAdapter = null;
  private Button mInviteButton = null;

  private MMX.EventListener mListener = new MMX.EventListener() {
    public boolean onMessageReceived(MMXMessage mmxMessage) {
      MMXChannel channel = mmxMessage.getChannel();
      if (channel != null) {
        if (RPSLS.MessageConstants.AVAILABILITY_TOPIC_NAME.equals(channel.getName())) {
          //handle the availability/unavailability of users
          RPSLS.Util.handleIncomingMessage(MainActivity.this, mmxMessage);
          updateAvailablePlayersView();
          return true;
        }
      } else {
        RPSLS.Util.handleIncomingMessage(MainActivity.this, mmxMessage);
        return true;
      }
      return false;
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mMainHandler = new Handler();

    mViewAnimator = (ViewAnimator) findViewById(R.id.viewAnimator);
    mFindOpponentButton = (Button) findViewById(R.id.btn_find_opponent);
    mUsernameView = (TextView) findViewById(R.id.username);
    mWinsView = (TextView) findViewById(R.id.wins);
    mLossesView = (TextView) findViewById(R.id.losses);
    mDrawsView = (TextView) findViewById(R.id.draws);
    mAvailablePlayersListView = (ListView) findViewById(R.id.available_player_listview);
    mInviteButton = (Button) findViewById(R.id.btn_invite);

    //start connection
    MMX.registerListener(mListener);
    mProfile = MyProfile.getInstance(this);

    mAvailablePlayersAdapter = new UserProfileAdapter(this, RPSLS.Util.getAvailablePlayers());
    mAvailablePlayersListView.setAdapter(mAvailablePlayersAdapter);
    mAvailablePlayersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mInviteButton.setEnabled(mAvailablePlayersListView.getCheckedItemCount() > 0);
      }
    });
    updateViewConnectionState();
  }

  protected void onDestroy() {
    MMX.unregisterListener(mListener);
    RPSLS.Util.publishAvailability(this, false);
    super.onDestroy();
  }

  private void startLoginActivity() {
    Intent loginIntent = new Intent(this, LoginActivity.class);
    startActivityForResult(loginIntent, REQUEST_LOGIN);
  }

  protected void onResume() {
    if (MMX.getCurrentUser() == null) {
      startLoginActivity();
    } else {
      //populate or update the view
      RPSLS.Util.publishAvailability(this, true);
      updateViewConnectionState();
      updateAvailablePlayersView();
    }
    //update stats
    updateStatsView();
    super.onResume();
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.d(TAG, "onActivityResult() request=" + requestCode + ", result=" + resultCode);
    if (requestCode == REQUEST_LOGIN) {
      if (resultCode == RESULT_OK) {
        //populate or update the view
        RPSLS.Util.publishAvailability(this, true);
        updateViewConnectionState();
        updateAvailablePlayersView();
      } else {
        finish();
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void updateViewConnectionState() {
    mMainHandler.post(new Runnable() {
      public void run() {
        boolean connected = MMX.getCurrentUser() != null;
        mFindOpponentButton.setEnabled(connected);
      }
    });
  }

  private void updateAvailablePlayersView() {
    mMainHandler.post(new Runnable() {
      public void run() {
        //save check state
        SparseBooleanArray checkedStates = mAvailablePlayersListView.getCheckedItemPositions();
        HashSet<UserProfile> checkedProfiles = new HashSet<UserProfile>();
        for (int i=0; i<checkedStates.size(); i++) {
          if (checkedStates.valueAt(i)) {
            checkedProfiles.add(mAvailablePlayersAdapter.getItem(checkedStates.keyAt(i)));
          }
        }
        //update the adapter
        mAvailablePlayersAdapter = new UserProfileAdapter(MainActivity.this, RPSLS.Util.getAvailablePlayers());
        mAvailablePlayersListView.setAdapter(mAvailablePlayersAdapter);

        //re-apply check state
        for (int i=0; i<mAvailablePlayersListView.getCount(); i++) {
          mAvailablePlayersListView.setItemChecked(i,
                  checkedProfiles.contains(mAvailablePlayersAdapter.getItem(i)));
        }
      }
    });
  }

  private void updateStatsView() {
    mMainHandler.post(new Runnable() {
      public void run() {
        Map<RPSLS.Outcome, Integer> outcomes = mProfile.getStats().getOutcomeCounts();
        mUsernameView.setText(mProfile.getUsername() != null ? mProfile.getUsername() : "");
        mWinsView.setText(String.valueOf(outcomes.get(RPSLS.Outcome.WIN)));
        mLossesView.setText(String.valueOf(outcomes.get(RPSLS.Outcome.LOSS)));
        mDrawsView.setText(String.valueOf(outcomes.get(RPSLS.Outcome.DRAW)));
      }
    });
  }

  public void doFindOpponent(View view) {
    //launch a view to show the latest people who have published their availability
    mViewAnimator.showNext();
    mViewAnimator.setInAnimation(this, android.R.anim.slide_in_left);
    mViewAnimator.setOutAnimation(this, android.R.anim.slide_out_right);
  }

  public void doBack(View view) {
    mViewAnimator.showPrevious();
    mViewAnimator.setInAnimation(this, R.anim.slide_in_right);
    mViewAnimator.setOutAnimation(this, R.anim.slide_out_left);
  }

  public void doInvite(View view) {
    //send out the invitations, then doBack
    synchronized (mAvailablePlayersAdapter) {
      SparseBooleanArray checkedItems = mAvailablePlayersListView.getCheckedItemPositions();
      ArrayList<UserProfile> checkedProfiles = new ArrayList<UserProfile>();
      for (int i = 0; i < checkedItems.size(); i++) {
        int position = checkedItems.keyAt(i);
        if (checkedItems.valueAt(i)) {
          checkedProfiles.add(mAvailablePlayersAdapter.getItem(position));
        }
      }
      if (RPSLS.Util.sendInvitations(this, checkedProfiles)) {
        Toast.makeText(this, "Invitations sent.", Toast.LENGTH_SHORT).show();
        doBack(view);
      } else {
        Toast.makeText(this, "No invitees were selected.", Toast.LENGTH_LONG).show();
      }
    }
  }

  private class UserProfileAdapter extends ArrayAdapter<UserProfile> {

    public UserProfileAdapter(Context context, List<UserProfile> userProfiles) {
      super(context, 0, userProfiles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if (convertView == null) {
//        convertView = getLayoutInflater().inflate(R.layout.availability_list_item, null);
        convertView = getLayoutInflater().inflate(R.layout.simple_list_item_checked, null);
      }
      UserProfile profile = getItem(position);
//      TextView usernameField = (TextView) convertView.findViewById(R.id.username);
//      TextView statsField = (TextView) convertView.findViewById(R.id.stats);
//      usernameField.setText(profile.getUsername());
//      statsField.setText(buildStatsString(profile));
      CheckedTextView text1 = (CheckedTextView) convertView;
      text1.setText(profile.getUsername());
      return convertView;
    }
  }

  private String buildStatsString(UserProfile profile) {
    UserProfile.Stats stats = profile.getStats();
    Map<RPSLS.Outcome, Integer> outcomes = stats.getOutcomeCounts();
    return getResources().getString(R.string.user_record) + outcomes.get(RPSLS.Outcome.WIN)
            + '-' + outcomes.get(RPSLS.Outcome.LOSS) + '-' + outcomes.get(RPSLS.Outcome.DRAW);
  }
}
