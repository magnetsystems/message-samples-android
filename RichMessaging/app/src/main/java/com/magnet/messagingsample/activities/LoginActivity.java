package com.magnet.messagingsample.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.magnet.messagingsample.R;
import com.magnet.messagingsample.models.MyProfile;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXUser;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoginActivity extends AppCompatActivity {

    final String TAG = "LoginActivity";

    Button btLogin;
    LoginButton btFacebookLogin;
    Button btRegister;
    EditText etEmail;
    EditText etPassword;

    private AtomicBoolean mLoginSuccess = new AtomicBoolean(false);
    private CallbackManager mCallbackManager = null;
    private MyProfile mProfile = null;

    private FacebookCallback<LoginResult> mLoginCallback = new FacebookCallback<LoginResult>() {
        public void onSuccess(LoginResult loginResult) {
            Log.d(TAG, "LoginCallback.onSuccess() " + loginResult.getAccessToken().getUserId());
            final GraphRequest request = new GraphRequest(loginResult.getAccessToken(), "me");
            Bundle params = new Bundle();
            params.putString("fields", "id,name,email");
            request.setParameters(params);
            AsyncTask.execute(new Runnable() {
                public void run() {
                    Log.d(TAG, "LoginCallback.onSuccess() lookup email");
                    GraphResponse response = request.executeAndWait();
                    JSONObject jsonResp = response.getJSONObject();
                    try {
                        String email = jsonResp.getString("email");
                        String name = jsonResp.getString("name");
                        String id = jsonResp.getString("id");
                        mProfile.setUsername(email);
                        mProfile.setPassword(id.getBytes()); //FIXME: Find a better way to generate this password
                        mProfile.setDisplayName(name);
                        attemptRegister(mProfile.getUsername(), mProfile.getPassword(), false);
                    } catch (final Exception ex) {
                        Log.e(TAG, "LoginCallback.onSuccess(): exception caught while getting identity", ex);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Exception caught while getting identity: " +
                                        ex.getMessage(), Toast.LENGTH_LONG).show();
                                LoginActivity.this.finish();
                            }
                        });
                    }
                }
            });
        }

        public void onCancel() {
            Log.d(TAG, "LoginCallback.onCancel()");
            LoginActivity.this.finish();
        }

        public void onError(FacebookException e) {
            Log.e(TAG, "LoginCallback.onError() ", e);
            Toast.makeText(LoginActivity.this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
            LoginActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        mProfile = MyProfile.getInstance(this);
        if (mProfile.getUsername() != null && Profile.getCurrentProfile() != null) {
            attemptRegister(mProfile.getUsername(), mProfile.getPassword(), false);
        }

        setContentView(R.layout.activity_login);

        btLogin = (Button) findViewById(R.id.btLogin);
        btFacebookLogin = (LoginButton) findViewById(R.id.btFacebookLogin);
        btRegister = (Button) findViewById(R.id.btRegister);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                attemptLogin(username, password.getBytes());
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                attemptRegister(username, password.getBytes(), true);
            }
        });

        mCallbackManager = CallbackManager.Factory.create();
        btFacebookLogin.registerCallback(mCallbackManager, mLoginCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mProfile.getUsername() != null && Profile.getCurrentProfile() != null) {
            attemptRegister(mProfile.getUsername(), mProfile.getPassword(), false);
        }
    }

    private void attemptLogin(String user, byte[] pass) {
        MMX.login(user, pass, new MMX.OnFinishedListener<Void>() {
            public void onSuccess(Void aVoid) {
                //if an EventListener has already been registered, start receiving messages
                MMX.start();
                goToUserSelectActivity();

            }

            public void onFailure(MMX.FailureCode failureCode, Throwable throwable) {
                Log.e(TAG, "attemptLogin() error: " + failureCode, throwable);
                if (MMX.FailureCode.SERVER_AUTH_FAILED.equals(failureCode)) {
                    //login failed, probably an incorrect password
                    Toast.makeText(LoginActivity.this, "Invalid username and/or password.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void attemptRegister(final String user, final byte[] pass, final boolean isNewUser) {
        MMXUser mmxUser = new MMXUser.Builder().username(user).displayName(user).build();
        mmxUser.register(pass, new MMXUser.OnFinishedListener<Void>() {
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "attemptRegister() success");
                mLoginSuccess.set(true);
                attemptLogin(user, pass);
            }

            public void onFailure(MMXUser.FailureCode failureCode, Throwable throwable) {
                if (MMXUser.FailureCode.REGISTRATION_INVALID_USERNAME.equals(failureCode)) {
                    Log.e(TAG, "attemptRegister() error: " + failureCode, throwable);
                    Toast.makeText(LoginActivity.this, "Sorry, that's not a valid username.", Toast.LENGTH_LONG).show();
                }
                if (MMXUser.FailureCode.REGISTRATION_USER_ALREADY_EXISTS.equals(failureCode)) {
                    if (isNewUser) {
                        Log.e(TAG, "attemptRegister() error: " + failureCode, throwable);
                        Toast.makeText(LoginActivity.this, "Sorry, this user already exists.", Toast.LENGTH_LONG).show();
                    } else {
                        attemptLogin(user, pass);
                    }
                }
                mLoginSuccess.set(false);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void goToUserSelectActivity() {
        Intent intent;
        intent = new Intent(LoginActivity.this, UserSelectActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
