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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);

        if (MMX.getCurrentUser() == null) {
            MMX.logout(null);
            Intent intent = new Intent(UserSelectActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        rvUsers = (RecyclerView) findViewById(R.id.rvUsers);

        userlist = new ArrayList<>();
        adapter = new UsersRecyclerViewAdapter(this, userlist);
        rvUsers.setAdapter(new SlideInBottomAnimationAdapter(adapter));
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvUsers.setLayoutManager(layoutManager);

        updateViewState();
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
        if (id == R.id.nav_logout) {
            LoginManager.getInstance().logOut();
            MMX.logout(null);
            Intent intent = new Intent(UserSelectActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.nav_refresh) {
            updateViewState();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
