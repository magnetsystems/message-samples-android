package com.magnet.messagingsample.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.magnet.messagingsample.R;
import com.magnet.messagingsample.adapters.UsersRecyclerViewAdapter;
import com.magnet.messagingsample.models.User;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXUser;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.adapters.SlideInBottomAnimationAdapter;

public class UserSelectActivity extends AppCompatActivity {

    final String TAG = "UserSelectActivity";

    RecyclerView rvUsers;
    List<User> userlist;

    UsersRecyclerViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);

        rvUsers = (RecyclerView) findViewById(R.id.rvUsers);

        userlist = new ArrayList<>();
        adapter = new UsersRecyclerViewAdapter(this, userlist);
        rvUsers.setAdapter(new SlideInBottomAnimationAdapter(adapter));
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvUsers.setLayoutManager(layoutManager);

        MMXUser.findByName("t", 20, new MMXUser.OnFinishedListener<ListResult<MMXUser>>() {
            public void onSuccess(ListResult<MMXUser> users) {
                refreshListView(users);
            }

            public void onFailure(MMXUser.FailureCode failureCode, Throwable throwable) {
                Log.e(TAG, "MMXUser.findByName() error: " + failureCode, throwable);
                Toast.makeText(UserSelectActivity.this, "Oops.  Please try again.", Toast.LENGTH_LONG).show();
            }
        });

    }

    protected void refreshListView(ListResult<MMXUser> users) {
        User user;
        if (users.totalCount > 0) {
            for (int i = 0; i < users.items.size(); i++) {
                user = new User();
                user.setUsername(users.items.get(i).getUsername());
                userlist.add(user);
            }
        }
        adapter.clear();
        adapter.addAll(userlist);
//        rvUsers.getAdapter().notifyDataSetChanged();
//            adapter.refreshAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_select, menu);
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
}
