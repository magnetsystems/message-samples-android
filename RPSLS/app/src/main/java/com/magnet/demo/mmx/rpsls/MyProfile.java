package com.magnet.demo.mmx.rpsls;

import android.content.Context;
import android.content.SharedPreferences;

public final class MyProfile extends UserProfile {
  private static final String TAG = MyProfile.class.getSimpleName();
  private static final String PREFERENCES_NAME = "MyProfile";
  private static final String PREF_USERNAME = "username";
  private static final String PREF_PASSWORD = "password";
  private static final String PREF_WINS = RPSLS.Outcome.WIN.name();
  private static final String PREF_LOSSES = RPSLS.Outcome.LOSS.name();
  private static final String PREF_DRAWS = RPSLS.Outcome.DRAW.name();
  private static final String PREF_ROCK_COUNT = RPSLS.Choice.ROCK.name();
  private static final String PREF_PAPER_COUNT = RPSLS.Choice.PAPER.name();
  private static final String PREF_SCISSORS_COUNT = RPSLS.Choice.SCISSORS.name();
  private static final String PREF_LIZARD_COUNT = RPSLS.Choice.LIZARD.name();
  private static final String PREF_SPOCK_COUNT = RPSLS.Choice.SPOCK.name();

  private static MyProfile sInstance = null;
  private Context mContext = null;
  private SharedPreferences mSharedPrefs = null;
  private byte[] mPassword = null;

  private MyProfile(Context context) {
    super();
    mContext = context;
    mSharedPrefs = mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    loadProfile();
  }

  private synchronized void loadProfile() {
    setUsername(mSharedPrefs.getString(PREF_USERNAME, null));
    String password = mSharedPrefs.getString(PREF_PASSWORD, null);
    mPassword = password != null ? password.getBytes() : null;
    int wins = mSharedPrefs.getInt(PREF_WINS, 0);
    int losses = mSharedPrefs.getInt(PREF_LOSSES, 0);
    int draws = mSharedPrefs.getInt(PREF_DRAWS, 0);
    int rockCount = mSharedPrefs.getInt(PREF_ROCK_COUNT, 0);
    int paperCount = mSharedPrefs.getInt(PREF_PAPER_COUNT, 0);
    int scissorsCount = mSharedPrefs.getInt(PREF_SCISSORS_COUNT, 0);
    int lizardCount = mSharedPrefs.getInt(PREF_LIZARD_COUNT, 0);
    int spockCount = mSharedPrefs.getInt(PREF_SPOCK_COUNT, 0);
    setStats(new Stats(wins, losses, draws, rockCount, paperCount, scissorsCount, lizardCount, spockCount));
  }

  public static synchronized MyProfile getInstance(Context context) {
    if (sInstance == null) {
      sInstance = new MyProfile(context.getApplicationContext());
    }
    return sInstance;
  }

  public byte[] getPassword() {
    return mPassword;
  }

  public void setUsername(String username) {
    super.setUsername(username);
    mSharedPrefs.edit().putString(PREF_USERNAME, username).apply();
  }

  public void setPassword(byte[] password) {
    mPassword = password;
    mSharedPrefs.edit().putString(PREF_PASSWORD, new String(password)).apply();
  }

  public void incrementCount(RPSLS.Choice choice, RPSLS.Outcome outcome) {
    Stats stats = getStats();
    int curCount = stats.mChoiceCounts.get(choice);
    curCount++;
    stats.mChoiceCounts.put(choice, curCount);
    mSharedPrefs.edit().putInt(choice.name(), curCount).apply();

    curCount = stats.mOutcomeCounts.get(outcome);
    curCount++;
    stats.mOutcomeCounts.put(outcome, curCount);
    mSharedPrefs.edit().putInt(outcome.name(), curCount).apply();
  }

}
