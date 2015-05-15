package com.magnet.demo.mmx.soapbox;

import android.content.Context;
import android.content.SharedPreferences;

public final class MyProfile extends UserProfile {
  private static final String TAG = MyProfile.class.getSimpleName();
  private static final String PREFERENCES_NAME = "MyProfile";
  private static final String PREF_USERNAME = "username";
  private static final String PREF_PASSWORD = "password";

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

}
