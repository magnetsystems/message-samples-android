package com.magnet.demo.mmx.soapbox;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.magnet.android.ApiCallback;
import com.magnet.android.ApiError;
import com.magnet.android.User;
import com.magnet.android.auth.model.UserRegistrationInfo;
import com.magnet.max.android.Max;
import com.magnet.mmx.client.api.MMX;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends Activity {
  private static final String TAG = LoginActivity.class.getSimpleName();

  // UI references.
  private AutoCompleteTextView mUsernameView;
  private EditText mPasswordView;
  private View mProgressView;
  private View mLoginFormView;

  private AtomicBoolean mConnecting = new AtomicBoolean(false);
  private MyProfile mProfile = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    //if have credentials or already connected, go to channel list activity
    mProfile = MyProfile.getInstance(this);
    setContentView(R.layout.activity_login);

    // Set up the login form.
    mUsernameView = (AutoCompleteTextView) findViewById(R.id.email);
    mUsernameView.setText(mProfile.getUsername());

    mPasswordView = (EditText) findViewById(R.id.password);

    Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
    mEmailSignInButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        attemptLogin(false);
      }
    });

    Button mRegisterButton = (Button) findViewById(R.id.register_button);
    mRegisterButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        attemptLogin(true);
      }
    });

    mLoginFormView = findViewById(R.id.login_form);
    mProgressView = findViewById(R.id.login_progress);
  }

  protected void onDestroy() {
    super.onDestroy();
  }

  /**
   * Attempts to sign in or register the account specified by the login form.
   * If there are form errors (invalid email, missing fields, etc.), the
   * errors are presented and no actual login attempt is made.
   */
  public void attemptLogin(boolean register) {
    if (mConnecting.get()) {
      return;
    }

    // Reset errors.
    mUsernameView.setError(null);
    mPasswordView.setError(null);

    // Store values at the time of the login attempt.
    final String username = mUsernameView.getText().toString();
    final String password = mPasswordView.getText().toString();

    boolean cancel = false;
    View focusView = null;

    // Check for a valid password, if the user entered one.
    if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
      mPasswordView.setError(getString(R.string.error_invalid_password));
      focusView = mPasswordView;
      cancel = true;
    }

    // Check for a valid username.
    if (TextUtils.isEmpty(username)) {
      mUsernameView.setError(getString(R.string.error_field_required));
      focusView = mUsernameView;
      cancel = true;
    }

    if (cancel) {
      // There was an error; don't attempt login and focus the first
      // form field with an error.
      focusView.requestFocus();
    } else {
      // Show a progress spinner, and kick off a background task to
      // perform the user login attempt.
      mProfile.setUsername(username);
      mProfile.setPassword(password.getBytes());
      showProgress(true);
      mConnecting.set(true);
      if (register) {
        UserRegistrationInfo userInfo = new UserRegistrationInfo.Builder()
                .userName(username)
                .password(password)
                .build();
        User.register(userInfo, new ApiCallback<User>() {
          @Override
          public void success(User user) {
            loginHelper(username, password);
          }

          @Override
          public void failure(final ApiError apiError) {
            Log.e(TAG, "register() error: " + apiError, apiError.getCause());
            mConnecting.set(false);

            runOnUiThread(new Runnable() {
              public void run () {
                Toast.makeText(LoginActivity.this, LoginActivity.this.getString(R.string.error_registration_generic)
                        + apiError.getMessage(), Toast.LENGTH_LONG).show();
                showProgress(false);
              }
            });
          }
        });
      } else {
        loginHelper(username, password);
      }
    }
  }

  private void loginHelper(String username, String password) {
    User.login(username, password, false, new ApiCallback<Boolean>() {
      public void success(Boolean aBoolean) {
        Max.initModule(MMX.getModule(), new ApiCallback<Boolean>() {
          public void success(Boolean aBoolean) {
            //login success
            MMX.start();
            ChannelsManager.getInstance(LoginActivity.this).provisionChannels();
            mConnecting.set(false);
            setResult(RESULT_OK);
            finish();
            runOnUiThread(new Runnable() {
              public void run() {
                showProgress(false);
              }
            });
          }

          public void failure(final ApiError apiError) {
            Log.e(TAG, "loginHelper() MMX init error: " + apiError.getMessage(), apiError.getCause());
            mConnecting.set(false);
            runOnUiThread(new Runnable() {
              public void run() {
                Toast.makeText(LoginActivity.this,
                        "Error occured: " + apiError.getMessage(), Toast.LENGTH_LONG).show();
                showProgress(false);
              }
            });
          }
        });
      }

      public void failure(final ApiError apiError) {
        Log.e(TAG, "loginHelper() Login error: " + apiError.getMessage(), apiError.getCause());
        mConnecting.set(false);
        runOnUiThread(new Runnable() {
          public void run() {
            Toast.makeText(LoginActivity.this,
                    "Error occured: " + apiError.getMessage(), Toast.LENGTH_LONG).show();
            showProgress(false);
          }
        });
      }
    });
  }

  private boolean isPasswordValid(String password) {
    //TODO: Replace this with your own logic
    return password.length() >= 4;
  }

  /**
   * Shows the progress UI and hides the login form.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  public void showProgress(final boolean show) {
    // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
    // for very easy animations. If available, use these APIs to fade-in
    // the progress spinner.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
      int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

      mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
      mLoginFormView.animate().setDuration(shortAnimTime).alpha(
              show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
      });

      mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
      mProgressView.animate().setDuration(shortAnimTime).alpha(
              show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
      });
    } else {
      // The ViewPropertyAnimator APIs are not available, so simply show
      // and hide the relevant UI components.
      mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
      mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
  }
}

