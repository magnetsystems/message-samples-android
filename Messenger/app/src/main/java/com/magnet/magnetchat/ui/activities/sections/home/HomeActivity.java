package com.magnet.magnetchat.ui.activities.sections.home;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.callbacks.BaseActivityCallback;
import com.magnet.magnetchat.constants.AppFragment;
import com.magnet.magnetchat.helpers.ChannelHelper;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.ui.activities.abs.BaseActivity;
import com.magnet.magnetchat.ui.activities.sections.login.LoginActivity;
import com.magnet.magnetchat.ui.custom.CustomDrawerButton;
import com.magnet.magnetchat.ui.fragments.BaseFragment;
import com.magnet.magnetchat.ui.fragments.EventFragment;
import com.magnet.magnetchat.ui.fragments.HomeFragment;
import com.magnet.magnetchat.ui.fragments.SupportFragment;
import com.magnet.magnetchat.util.AppLogger;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends BaseActivity implements BaseActivityCallback, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = HomeActivity.class.getSimpleName();


    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.toolbarDrawerButton)
    CustomDrawerButton drawerButton;
    @InjectView(R.id.toolbarTitle)
    TextView toolbarTitle;

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawer;

    @InjectView(R.id.nav_view)
    NavigationView navView;

    //@InjectView(R.id.llUserProfile)
    LinearLayout llUserProfile;

    //@InjectView(R.id.ivUserAvatar)
    CircleImageView ivUserAvatar;

    private AppFragment currentFragment;

    private TextView tvMenuItemCountNew;
    private LinearLayout llMenuItemNew;

    private int unreadSupport = 0;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "\n---------------------------------\nHomeActivity created\n---------------------------------\n");

        setSupportActionBar(toolbar);

        View headerView = navView.getHeaderView(0);
        llUserProfile = (LinearLayout) headerView.findViewById(R.id.llUserProfile);
        ivUserAvatar = (CircleImageView) headerView.findViewById(R.id.ivUserAvatar);

        setOnClickListeners(drawerButton, llUserProfile);

        Menu menu = navView.getMenu();
        menu.getItem(menu.size() - 1).setTitle("Version " + getVersionName());
        if (!UserHelper.isMagnetSupportMember()) {
            menu.getItem(1).setVisible(false);
        } else {
            View menuSupportView = menu.findItem(R.id.nav_support).getActionView();
            if (menuSupportView != null) {
                llMenuItemNew = (LinearLayout) menuSupportView.findViewById(R.id.llMenuItemNew);
                tvMenuItemCountNew = (TextView) menuSupportView.findViewById(R.id.tvMenuItemCountNew);
            }
        }

        navView.setNavigationItemSelectedListener(this);

        drawer.openDrawer(GravityCompat.START);

        setFragment(AppFragment.HOME);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                setFragment(AppFragment.HOME);
                break;
            case R.id.nav_support:
                drawerButton.hideWarning();
                unreadSupport = 0;
                if (llMenuItemNew != null) {
                    llMenuItemNew.setVisibility(View.INVISIBLE);
                }
                setFragment(AppFragment.SUPPORT);
                break;
            case R.id.nav_signout:
                UserHelper.logout(logoutListener);
                break;
            case R.id.nav_about:

                break;
            default:
                setFragment(AppFragment.HOME);
                break;
        }
        closeDrawer();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setTitle("");

        closeDrawer();

        if (User.getCurrentUser() != null) {
            if (UserHelper.isMagnetSupportMember()) {
                MMX.registerListener(homeMessageReceiver);
            }
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
    protected void onPause() {
        MMX.unregisterListener(homeMessageReceiver);
        super.onPause();
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

    private String getVersionName() {
        String versionName = "1.0";
        try {
            versionName = this.getPackageManager()
                .getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "Error when getting version", e);
        }

        return versionName;
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
                if (User.getCurrentUser() != null) {
                    toolbarTitle.setText(User.getCurrentUser().getDisplayName());
                }
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
    private BaseFragment getFragment(AppFragment appFragment, BaseActivityCallback baseActivity) {
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

    /**
     * Receiver which check if drawer button should show indicator, that support section has unread message
     */
    private MMX.EventListener homeMessageReceiver = new MMX.EventListener() {
        @Override
        public boolean onMessageReceived(MMXMessage mmxMessage) {
            if (mmxMessage != null && mmxMessage.getChannel() != null) {
                MMXChannel channel = mmxMessage.getChannel();
                if (currentFragment == AppFragment.HOME && channel.getName().equalsIgnoreCase(ChannelHelper.ASK_MAGNET)) {
                    unreadSupport++;
                    drawerButton.showWarning();
                    if (tvMenuItemCountNew != null) {
                        llMenuItemNew.setVisibility(View.VISIBLE);
                        tvMenuItemCountNew.setText(String.valueOf(unreadSupport));
                    }
                }
            }
            return false;
        }
    };

}
