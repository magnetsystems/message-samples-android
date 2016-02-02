package com.magnet.messagingsample.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;
import com.magnet.messagingsample.R;
import com.magnet.messagingsample.adapters.UsersRecyclerViewAdapter;
import com.magnet.mmx.client.api.MMX;

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
        User.search("%", 50, 0, null, new ApiCallback<List<User>>() {
            @Override
            public void success(List<User> users) {
                refreshListView(users.size() > 0 ? users : null);
            }

            @Override
            public void failure(ApiError apiError) {
                Log.e(TAG, "MMXUser.findByName() error: " + apiError, apiError.getCause());
                refreshListView(null);
            }
        });
    }

    protected void refreshListView(final List<User> users) {
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
