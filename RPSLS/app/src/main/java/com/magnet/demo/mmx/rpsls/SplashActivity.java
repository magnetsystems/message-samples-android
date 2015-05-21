package com.magnet.demo.mmx.rpsls;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

/**
 * This activity is meant for showing the splash screen.
 */
public class SplashActivity extends Activity {
  private static boolean sIsShown = false;
  private Handler mHandler = new Handler();

  private Runnable mLaunchMainActivityRunnable = new Runnable() {
    public void run() {
      Intent intent = new Intent(SplashActivity.this, MainActivity.class);
      startActivity(intent);
      SplashActivity.this.finish();
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_splash);
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);

    if (sIsShown) {
      mLaunchMainActivityRunnable.run();
    } else {
      sIsShown = true;
      mHandler.postDelayed(mLaunchMainActivityRunnable, 2000);
    }
  }

  public void doHideSplash(View view) {
    mHandler.removeCallbacks(mLaunchMainActivityRunnable);
    mLaunchMainActivityRunnable.run();
  }
}
