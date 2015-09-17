package com.magnet.messagingsample.activities;

import android.content.Intent;
import android.os.AsyncTask;
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

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.magnet.messagingsample.R;
import com.magnet.messagingsample.adapters.UsersRecyclerViewAdapter;
import com.magnet.messagingsample.models.MyProfile;
import com.magnet.messagingsample.models.User;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import jp.wasabeef.recyclerview.animators.adapters.SlideInBottomAnimationAdapter;

public class UserSelectActivity extends AppCompatActivity {

    final String TAG = "UserSelectActivity";

    RecyclerView rvUsers;
    List<User> userlist;

    UsersRecyclerViewAdapter adapter;

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
                        setupMMX();
                    } catch (final Exception ex) {
                        Log.e(TAG, "LoginCallback.onSuccess(): exception caught while getting identity", ex);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(UserSelectActivity.this, "Exception caught while getting identity: " +
                                        ex.getMessage(), Toast.LENGTH_LONG).show();
                                UserSelectActivity.this.finish();
                            }
                        });
                    }
                }
            });
        }

        public void onCancel() {
            Log.d(TAG, "LoginCallback.onCancel()");
            UserSelectActivity.this.finish();
        }

        public void onError(FacebookException e) {
            Log.e(TAG, "LoginCallback.onError() ", e);
            Toast.makeText(UserSelectActivity.this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
            UserSelectActivity.this.finish();
        }
    };

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

        mProfile = MyProfile.getInstance(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, mLoginCallback);
        if (mProfile.getUsername() == null || Profile.getCurrentProfile() == null) {
            //go login
            HashSet<String> permissions = new HashSet<String>();
            permissions.add("user_friends");
            permissions.add("email");
            LoginManager.getInstance().logInWithReadPermissions(this, permissions);
        } else {
            setupMMX();
        }
        // TODO: this doesnt return all users
//        HashSet names = new HashSet();
//        names.add("");
//        MMXUser.findByNames(names, new MMXUser.OnFinishedListener<HashMap<String, MMXUser>>() {
//            @Override
//            public void onSuccess(HashMap<String, MMXUser> stringMMXUserHashMap) {
//                ArrayList<MMXUser> users = new ArrayList<>();
//                for (MMXUser user : stringMMXUserHashMap.values()) {
//                    users.add(user);
//                }
//                refreshListView(users);
//            }
//
//            @Override
//            public void onFailure(MMXUser.FailureCode failureCode, Throwable throwable) {
//                Log.e(TAG, "MMXUser.findByNames() error: " + failureCode, throwable);
//                refreshListView(null);
//            }
//        });

    }

    private void updateViewState() {
        // return all users
        MMXUser.findByName("%", 50, new MMXUser.OnFinishedListener<ListResult<MMXUser>>() {
            public void onSuccess(ListResult<MMXUser> users) {
                refreshListView(users.totalCount > 0 ? users.items : null);
            }

            public void onFailure(MMXUser.FailureCode failureCode, Throwable throwable) {
                Log.e(TAG, "MMXUser.findByName() error: " + failureCode, throwable);
                refreshListView(null);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setupMMX() {
        // Register this activity as a listener to receive and show incoming
        // messages.  See #onDestroy for the unregister call.
        MMXUser internalUser = new MMXUser.Builder()
                .username(mProfile.getUsername())
                .displayName(mProfile.getDisplayName())
                .build();
        internalUser.register(mProfile.getPassword(), new MMXUser.OnFinishedListener<Void>() {
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "register user succeeded");
                loginHelper();
            }

            public void onFailure(MMXUser.FailureCode failureCode, Throwable throwable) {
                Log.d(TAG, "register user failed because: " + failureCode);
                loginHelper();
            }
        });
    }

    private void loginHelper() {
        MMX.login(mProfile.getUsername(), mProfile.getPassword(), new MMX.OnFinishedListener<Void>() {
            public void onSuccess(Void aVoid) {
                mLoginSuccess.set(true);
                MMX.enableIncomingMessages(true);
                updateViewState();
            }

            public void onFailure(MMX.FailureCode failureCode, Throwable e) {
                mLoginSuccess.set(false);
                updateViewState();
            }
        });
    }

    protected void refreshListView(final List<MMXUser> users) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.clear();
                if (users != null && users.size() > 0) {
                    adapter.addAll(users);
                    rvUsers.getAdapter().notifyDataSetChanged();
                } else {
                    Toast.makeText(UserSelectActivity.this, "No users found.", Toast.LENGTH_LONG).show();
                }
            }
        });
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
