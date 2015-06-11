package com.magnet.demo.mmx.rpsls;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.magnet.mmx.client.MMXClient;
import com.magnet.mmx.client.MMXTask;

import java.util.Random;


public class GameActivity extends Activity {
  private static final String TAG = GameActivity.class.getSimpleName();
  private MMXClient mClient = null;
  private RPSLS.Game mGame = null;
  private TextView mOpponent = null;
  private TextView mResult = null;
  private ImageView mResultImage = null;
  private ImageView mMyChoice = null;
  private ImageView mOpponentChoice = null;
  private ProgressBar mOpponentProgress = null;
  private ViewAnimator mViewAnimator = null;
  private Button mBackButton = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mClient = MMXClient.getInstance(this, R.raw.rpsls);
    Intent startIntent = getIntent();
    String gameId = startIntent.getStringExtra(RPSLS.Util.EXTRA_GAME_ID);
    mGame = RPSLS.Util.getGame(gameId);
    setContentView(R.layout.activity_game);
    mViewAnimator = (ViewAnimator) findViewById(R.id.viewAnimator);
    mResult = (TextView) findViewById(R.id.result);
    mResultImage = (ImageView) findViewById(R.id.resultImage);
    mMyChoice = (ImageView) findViewById(R.id.my_choice);
    mOpponentChoice = (ImageView) findViewById(R.id.opponent_choice);
    mOpponentProgress = (ProgressBar) findViewById(R.id.opponent_progress);
    mBackButton = (Button) findViewById(R.id.btn_back);
    mOpponent = (TextView) findViewById(R.id.opponent);
    mOpponent.setText("Playing against " + mGame.getSelectedOpponent().getUsername());

    //randomize choice buttons
    int numChoices = RPSLS.Choice.values().length;
    RPSLS.Choice[] randomChoices = new RPSLS.Choice[numChoices];
    System.arraycopy(RPSLS.Choice.values(), 0,randomChoices, 0, numChoices);
    Random random = new Random();
    for (int i=0; i<randomChoices.length; i++) {
      int swapIndex = random.nextInt(numChoices);
      RPSLS.Choice tmp = randomChoices[swapIndex];
      randomChoices[swapIndex] = randomChoices[i];
      randomChoices[i] = tmp;
    }
    ImageButton choiceBtn = (ImageButton)findViewById(R.id.choice1);
    setChoiceButton(choiceBtn, randomChoices[0]);
    choiceBtn = (ImageButton)findViewById(R.id.choice2);
    setChoiceButton(choiceBtn, randomChoices[1]);
    choiceBtn = (ImageButton)findViewById(R.id.choice3);
    setChoiceButton(choiceBtn, randomChoices[2]);
    choiceBtn = (ImageButton)findViewById(R.id.choice4);
    setChoiceButton(choiceBtn, randomChoices[3]);
    choiceBtn = (ImageButton)findViewById(R.id.choice5);
    setChoiceButton(choiceBtn, randomChoices[4]);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_game, menu);
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

  public void doChoice(View view) {
    Log.d(TAG, "doChoice(): " + view.getTag());
    mViewAnimator.showNext();
    String tag = (String)view.getTag();
    final RPSLS.Choice myChoice = RPSLS.Choice.valueOf(tag);
    mMyChoice.setImageResource(myChoice.getResourceId());
    MMXTask<RPSLS.Game.Result> task = new MMXTask<RPSLS.Game.Result>(mClient) {
      @Override
      public void onResult(RPSLS.Game.Result result) {
        Log.d(TAG, "onResult(): result =" + result);
        RPSLS.Util.removeGame(mGame.getGameId());
        updateViewWithResults(result);
      }

      @Override
      public void onException(Throwable exception) {
        super.onException(exception);
      }

      @Override
      public RPSLS.Game.Result doRun(MMXClient mmxClient) throws Throwable {
        //start progress spinner
        return mGame.getResult(GameActivity.this, mmxClient, myChoice);
      }
    };
    task.execute();
  }

  private void updateViewWithResults(final RPSLS.Game.Result result) {
    runOnUiThread(new Runnable() {
      public void run() {
        mOpponentProgress.setVisibility(View.GONE);
        mOpponentChoice.setVisibility(View.VISIBLE);
        mBackButton.setEnabled(true);
        mResult.setText(buildResultString(result));
        if (result != null) {
          if (result.outcome == RPSLS.Outcome.WIN) {
            mResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
          } else if (result.outcome == RPSLS.Outcome.LOSS) {
            mResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
          }
          if (result.resourceId != 0) {
            mResultImage.setImageResource(result.resourceId);
          }
          mMyChoice.setImageResource(result.myChoice.getResourceId());
          if (result.opponentChoice != null) {
            mOpponentChoice.setImageResource(result.opponentChoice.getResourceId());
          }
        }
      }
    });
  }

  private String buildResultString(RPSLS.Game.Result result) {
    StringBuffer sb = new StringBuffer();
    //TODO: get from resources and don't use the enum names
    if (result != null) {
      switch (result.outcome) {
        case WIN:
          sb.append("Congratulations! You WON!");
//          sb.append(result.myChoice);
//          sb.append(" ");
//          sb.append(result.how);
//          sb.append(" ");
//          sb.append(result.opponentChoice);
          break;
        case LOSS:
          sb.append("Sorry! You LOST!");
//          sb.append(result.opponentChoice);
//          sb.append(" ");
//          sb.append(result.how);
//          sb.append(" ");
//          sb.append(result.myChoice);
          break;
        case DRAW:
          sb.append("Even Steven.  Nobody won...");
          break;
      }
    } else {
      //waited too long
      sb.append("There was no response from " + mGame.getSelectedOpponent().getUsername());
    }
    return sb.toString();
  }

  public void doBack(View view) {
    this.onBackPressed();
  }

  private void setChoiceButton(ImageButton button, RPSLS.Choice choice) {
    button.setTag(choice.name());
    button.setImageResource(choice.getResourceId());
    button.setContentDescription(choice.name());
  }
}
