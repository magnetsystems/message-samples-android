package com.magnet.magnetchat.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    private String username;

    private HomeFragment homeFragment;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (User.getCurrentUser() != null) {
            username = User.getCurrentUser().getDisplayName();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(username);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView navigationName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.homeUserName);
        navigationName.setText(username);

        showHomeFragment();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHome:
                showHomeFragment();
                break;
            case R.id.menuEvents:
                break;
            case R.id.menuSignOut:
                UserHelper.logout(logoutListener);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {

    }

    private void replaceToFragment(Fragment fragment) {
        replace(fragment, R.id.homeContent);
    }

    private void showHomeFragment() {
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        replaceToFragment(homeFragment);
    }

    private UserHelper.OnLogoutListener logoutListener = new UserHelper.OnLogoutListener() {
        @Override
        public void onSuccess() {
            goToHomeActivity();
        }

        @Override
        public void onFailedLogin(ApiError apiError) {
            //showMessage("Can't sign out");
            // Go to home acitivity anyway
            goToHomeActivity();
        }

        private void goToHomeActivity() {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }
    };

}
