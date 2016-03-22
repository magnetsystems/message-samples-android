package com.magnet.magntetchatapp.ui.activities.section.home;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.magnet.magntetchatapp.R;
import com.magnet.magntetchatapp.callbacks.OnActivityEventCallback;
import com.magnet.magntetchatapp.factories.FragmentFactory;
import com.magnet.magntetchatapp.mvp.api.ChannelsListContract;
import com.magnet.magntetchatapp.ui.activities.abs.BaseActivity;
import com.magnet.magntetchatapp.ui.activities.section.edit.EditProfileActivity;
import com.magnet.magntetchatapp.ui.activities.section.login.LoginActivity;
import com.magnet.max.android.User;

import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends BaseActivity {

    private static final String TAG = "HomeActivity";

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawer;
    @InjectView(R.id.nav_view)
    NavigationView navigationView;

    private FragmentFactory.FragmentType fragmentType;

    private ActionBarDrawerToggle toggle;

    //DRAWER HEADER
    private CircleImageView imageAvatar;
    private View buttonEditProfile;
    private AppCompatTextView labelUserFullName;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected int getMenuId() {
        return NONE_MENU;
    }

    @Override
    protected void onCreateActivity() {
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(navigationListener);
        onHeaderInitialization();
        setFragment(FragmentFactory.FragmentType.HOME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onHeaderCustomize();
        onCustomizeActionBar();
    }

    /**
     * Method which provide the customization of the Action Bar
     */
    private void onCustomizeActionBar() {
        User user = User.getCurrentUser();
        if (user != null) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(String.format("%s %s", user.getFirstName(), user.getLastName()).toUpperCase());
            }
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(String.format("Conversation list").toUpperCase());
            }
        }
    }

    /**
     * Method which provide to set of the fragment by type
     *
     * @param fragmentType fragment type
     */
    private void setFragment(FragmentFactory.FragmentType fragmentType) {
        if (this.fragmentType == null || this.fragmentType != fragmentType) {
            this.fragmentType = fragmentType;
            replace(FragmentFactory.getFragment(fragmentType, eventCallback), R.id.fragment_content);
        }
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Overriden method for the OnClickListener
     *
     * @param v current view
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonInfo) {
            navigateToEditProfile();
        }
    }

    /**
     * Method which provide the navigating to the EditProfileActivity
     */
    private void navigateToEditProfile() {
        drawer.closeDrawer(GravityCompat.START);
        runOnMainThread(0.4, new OnActionPerformer() {
            @Override
            public void onActionPerform() {
                startActivity(EditProfileActivity.class, false);
            }
        });
    }

    /**
     * Methdo which provide th header initialization
     */
    private void onHeaderInitialization() {
        final View headerView = navigationView.getHeaderView(0);

        if (headerView == null) {
            return;
        }

        imageAvatar = (CircleImageView) headerView.findViewById(R.id.imageAvatar);
        buttonEditProfile = headerView.findViewById(R.id.buttonInfo);
        labelUserFullName = (AppCompatTextView) headerView.findViewById(R.id.textUserName);
    }

    /**
     * Method which provide the header customization
     */
    private void onHeaderCustomize() {
        final User user = User.getCurrentUser();

        if (buttonEditProfile != null) {
            buttonEditProfile.setOnClickListener(this);
        }

        if (user != null) {
            if (labelUserFullName != null) {
                labelUserFullName.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
            }
            if (imageAvatar != null && user.getAvatarUrl() != null) {
                Glide.with(this)
                        .load(user.getAvatarUrl())
                        .listener(glideDownloadListener)
                        .placeholder(R.drawable.image_no_avatar)
                        .centerCrop()
                        .into(imageAvatar);
            }
        }
    }

    //CALLBACKS

    /**
     * Callback which provide the listening of the Glide events
     */
    private final RequestListener<String, GlideDrawable> glideDownloadListener = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            Log.e(TAG, e.toString());
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            imageAvatar.setImageDrawable(resource);
            return false;
        }
    };

    /**
     * Callback which provide the listening of the Navigation drawer
     */
    private final NavigationView.OnNavigationItemSelectedListener navigationListener = new NavigationView.OnNavigationItemSelectedListener() {

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.home_screen) {
                setFragment(FragmentFactory.FragmentType.HOME);
            } else if (id == R.id.sign_out) {
                User.logout();
                runOnMainThread(0.3, new OnActionPerformer() {
                    @Override
                    public void onActionPerform() {
                        startActivityWithClearTop(LoginActivity.class);
                    }
                });
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    };

    /**
     * Callback which provide to send action from Fragment to activity
     */
    private final OnActivityEventCallback eventCallback = new OnActivityEventCallback() {
        @Override
        public void onEventReceived(Event event, Object... objects) {

        }
    };

}
