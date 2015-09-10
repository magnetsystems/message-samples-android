package com.magnet.messagingsample.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.magnet.messagingsample.R;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXUser;

public class LoginActivity extends AppCompatActivity {

    final String TAG = "LoginActivity";

    Button btLogin;
    Button btRegister;
    EditText etEmail;
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btLogin = (Button) findViewById(R.id.btLogin);
        btRegister = (Button) findViewById(R.id.btRegister);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                attemptLogin(username, password);
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                attemptRegister(username, password);
            }
        });

    }

    private void attemptLogin(String user, String pass) {
        MMX.login(user, pass.getBytes(), new MMX.OnFinishedListener<Void>() {
            public void onSuccess(Void aVoid) {
                //if an EventListener has already been registered, start receiving messages
                MMX.enableIncomingMessages(true);
                goToUserSelectActivity();

            }

            public void onFailure(MMX.FailureCode failureCode, Throwable throwable) {
                Log.e(TAG, "attemptLogin() error: " + failureCode, throwable);
                if (MMX.FailureCode.SERVER_AUTH_FAILED.equals(failureCode)) {
                    //login failed, probably an incorrect password
                    Toast.makeText(LoginActivity.this, "Oops.  Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void attemptRegister(final String user, final String pass) {
        MMXUser mmxUser = new MMXUser.Builder().username(user).build();
        mmxUser.register(pass.getBytes(), new MMXUser.OnFinishedListener<Void>() {
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "attemptRegister() success");
                attemptLogin(user, pass);
            }

            public void onFailure(MMXUser.FailureCode failureCode, Throwable throwable) {
                Log.e(TAG, "attemptRegister() error: " + failureCode, throwable);
                if (MMXUser.FailureCode.REGISTRATION_INVALID_USERNAME.equals(failureCode)) {
                    Toast.makeText(LoginActivity.this, "Sorry, thats not a valid username.", Toast.LENGTH_LONG).show();
                }
                if (MMXUser.FailureCode.REGISTRATION_USER_ALREADY_EXISTS.equals(failureCode)) {
                    Toast.makeText(LoginActivity.this, "User already exists!", Toast.LENGTH_LONG).show();
                }
            }
        });
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
