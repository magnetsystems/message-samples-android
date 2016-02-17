package com.magnet.magnetchat.ui.activities.sections.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.callbacks.BaseActivityCallback;
import com.magnet.magnetchat.constants.AppFragment;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.ui.activities.abs.BaseActivity;
import com.magnet.magnetchat.ui.activities.sections.login.LoginActivity;
import com.magnet.magnetchat.ui.adapters.MenuAdapter;
import com.magnet.magnetchat.ui.custom.CustomDrawerButton;
import com.magnet.magnetchat.ui.custom.FTextView;
import com.magnet.magnetchat.ui.fragments.BaseFragment;
import com.magnet.magnetchat.ui.fragments.EventFragment;
import com.magnet.magnetchat.ui.fragments.HomeFragment;
import com.magnet.magnetchat.ui.fragments.SupportFragment;
import com.magnet.magnetchat.util.AppLogger;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;

import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends BaseActivity implements BaseActivityCallback {
    private static final String TAG = HomeActivity.class.getSimpleName();

    @InjectView(R.id.listHomeDrawer)
    ListView listHomeDrawer;
    @InjectView(R.id.textUserName)
    FTextView textUserFullName;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.toolbarDrawerButton)
    CustomDrawerButton drawerButton;
    @InjectView(R.id.toolbarTitle)
    TextView toolbarTitle;

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawer;

    @InjectView(R.id.llUserProfile)
    LinearLayout llUserProfile;

    @InjectView(R.id.ivUserAvatar)
    CircleImageView ivUserAvatar;

    private AppFragment currentFragment;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "\n---------------------------------\nHomeActivity created\n---------------------------------\n");

        setSupportActionBar(toolbar);

        setOnClickListeners(drawerButton, llUserProfile);

        listHomeDrawer.setOnItemClickListener(menuClickListener);

        if (UserHelper.isMagnetSupportMember()) {
            String[] entries = getResources().getStringArray(R.array.entries_support_home_drawer);
            listHomeDrawer.setAdapter(new MenuAdapter(this, entries));
            listHomeDrawer.setOnItemClickListener(menuForSupportClickListener);
        }

        drawer.openDrawer(GravityCompat.START);

        setFragment(AppFragment.HOME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setTitle("");

        closeDrawer();

        if (User.getCurrentUser() != null) {
            textUserFullName.setSafeText(User.getCurrentUser().getDisplayName());
            if (currentFragment == AppFragment.HOME) {
                toolbarTitle.setText(User.getCurrentUser().getDisplayName());
            }

            if (null != User.getCurrentUser().getAvatarUrl()) {
                Glide.with(this)
                        .load(User.getCurrentUser().getAvatarUrl())
                        .placeholder(R.mipmap.ic_user)
                                //.signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                        .centerCrop()
                        .into(ivUserAvatar);
            }
        } else {
            Log.w(TAG, "CurrentUser is null, logout");
            UserHelper.logout(logoutListener);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbarDrawerButton:
                switchDrawer();
                break;
            case R.id.llUserProfile:
                startActivity(HomeEditProfileActivity.class);
                break;
        }
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
                toolbarTitle.setText(User.getCurrentUser().getDisplayName());
                break;
            case SUPPORT:
                drawerButton.hideWarning();
                toolbarTitle.setText("Support");
                break;
        }
        replace(getFragment(fragment, this), R.id.container, fragment.name());
    }

    /**
     * Creates new fragment for selected menu item
     *
     * @param appFragment  type of fragment
     * @param baseActivity activity for fragment
     * @return
     */
    public BaseFragment getFragment(AppFragment appFragment, BaseActivityCallback baseActivity) {
        BaseFragment baseFragment;
//                (BaseFragment) getSupportFragmentManager().findFragmentByTag(appFragment.name());
//        if (null == baseFragment) {
        switch (appFragment) {
            case HOME:
                baseFragment = new HomeFragment();
                break;
            case SUPPORT:
                baseFragment = new SupportFragment();
                break;
            case EVENTS:
                baseFragment = new EventFragment();
                break;

            default:
                baseFragment = new HomeFragment();
                break;
        }
        baseFragment.setBaseActivityCallback(baseActivity);
//        }
        return baseFragment;
    }

    @Override
    public void onReceiveFragmentEvent(Event event) {

    }

    /**
     * If drawer is opened, closes it. If was already closed, opens
     */
    private void switchDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    private void closeDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
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
                    UserHelper.logout(logoutListener);
                    break;
                default:
                    setFragment(AppFragment.HOME);
                    break;
            }

        }
    };

    /**
     * Listener which provide the menu item functional for support member
     */
    private final AdapterView.OnItemClickListener menuForSupportClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            drawer.closeDrawer(GravityCompat.START);
            switch (position) {
                case 0:
                    setFragment(AppFragment.HOME);
                    break;
                case 1:
                    setFragment(AppFragment.SUPPORT);
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
