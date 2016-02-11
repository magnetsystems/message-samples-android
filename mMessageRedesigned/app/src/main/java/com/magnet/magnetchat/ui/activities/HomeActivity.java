package com.magnet.magnetchat.ui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.callbacks.BaseActivityCallback;
import com.magnet.magnetchat.constants.AppFragment;
import com.magnet.magnetchat.factories.FragmentFactory;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.ui.custom.FTextView;
import com.magnet.magnetchat.util.AppLogger;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;

import butterknife.InjectView;

public class HomeActivity extends BaseActivity implements BaseActivityCallback {

    @InjectView(R.id.listHomeDrawer)
    ListView listHomeDrawer;
    @InjectView(R.id.textUserName)
    FTextView textUserFullName;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.viewEvents)
    View viewEvents;

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    private User currentUser;

    private AppFragment currentFragment;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (User.getCurrentUser() != null) {
            currentUser = User.getCurrentUser();
            textUserFullName.setSafeText(currentUser.getDisplayName());
            toolbar.setTitle(currentUser.getDisplayName());
        }

        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        listHomeDrawer.setOnItemClickListener(menuClickListener);

        setFragment(AppFragment.HOME);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
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

    @Override
    public void onClick(View v) {

    }

    /**
     * method which provide the setting of the current fragment co container view
     *
     * @param fragment current fragment type
     */
    private void setFragment(AppFragment fragment) {
        AppLogger.info(this, "setFragment: " + fragment + " : " + currentFragment);

        if (fragment.equals(currentFragment)) {
            return;
        }

        currentFragment = fragment;

        switch (fragment) {
            case HOME:
                viewEvents.setVisibility(View.VISIBLE);
                break;
            case EVENTS:
                viewEvents.setVisibility(View.GONE);
                break;
        }
        replace(FragmentFactory.getFragment(fragment, this), R.id.container);
    }

    @Override
    public void onReceiveFragmentEvent(Event event) {

    }

    /**
     * Listener which provide the menu item functional
     */
    private final AdapterView.OnItemClickListener menuClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            drawer.closeDrawer(GravityCompat.START);
            switch (position) {
                case 0:
                    setFragment(AppFragment.HOME);
                    break;
                case 1:
                    setFragment(AppFragment.EVENTS);
                    break;
                case 2:
                    UserHelper.logout(logoutListener);
                    break;
                default:
                    setFragment(AppFragment.HOME);
                    break;
            }

        }
    };

    /**
     * Listener which provide the logout functional
     */
    private final UserHelper.OnLogoutListener logoutListener = new UserHelper.OnLogoutListener() {
        @Override
        public void onSuccess() {
            goToHomeActivity();
        }

        @Override
        public void onFailedLogin(ApiError apiError) {
            goToHomeActivity();
        }

        private void goToHomeActivity() {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }
    };
}
