package com.magnet.magnetchat.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.BundleHelper;
import com.magnet.magnetchat.helpers.IntentHelper;
import com.magnet.magnetchat.model.MMXUserWrapper;
import com.magnet.magnetchat.presenters.UserListContract;
import com.magnet.magnetchat.ui.fragments.MMXAllUserListFragment;
import com.magnet.magnetchat.ui.fragments.MMXUserListFragment;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.MMXChannel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by aorehov on 13.05.16.
 */
public class MMXUsersActivity extends MMXBaseActivity implements UserListContract.OnSelectUserEvent, UserListContract.OnGetAllSelectedUsersListener {

    private MMXUserListFragment userListFragment;
    private Handler handler = new Handler();
    private String searchText = "";
    private MMXChannel mmxChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar uiToolbar = findView(R.id.toolbar);
        if (getSupportActionBar() == null) {
            setSupportActionBar(uiToolbar);
            uiToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } else {
            View view = uiToolbar.getRootView();
            uiToolbar.setVisibility(View.GONE);
            if (view instanceof ViewGroup) {
                ((ViewGroup) view).removeView(uiToolbar);
            }
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mmxChannel = BundleHelper.readMMXChannelFromBundle(getIntent().getExtras());

        userListFragment = new MMXAllUserListFragment();
        userListFragment.setOnUserSelectEventListener(this);
        userListFragment.setOnGetAllSelectedUsersListener(this);
        replace(userListFragment, R.id.mmx_chat, userListFragment.getTag());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_choose_user, menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final SearchView search = (SearchView) menu.findItem(R.id.mmx_search).getActionView();
            search.setQueryHint(getString(R.string.mmx_users_search_hint));
            search.setOnQueryTextListener(queryTextListener);
        }
        return true;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_mmxchat;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.mmx_create) {
            doCreateAction();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    private void doCreateAction() {
        userListFragment.doGetAllSelectedUsersEvent();
    }

    private final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            searchText = newText;

            handler.removeCallbacks(DO_SEARCH);
            handler.postDelayed(DO_SEARCH, 500);

            return false;
        }
    };

    private Runnable DO_SEARCH = new Runnable() {
        @Override
        public void run() {
            userListFragment.search(searchText);
        }
    };

    @Override
    public void onSelectEvent(MMXUserWrapper wrapper) {
    }

    @Override
    public void onGetAllSelectedUsers(List<MMXUserWrapper> selectedUsers) {
        List<User> users = new ArrayList<>(selectedUsers.size());
        Iterator<MMXUserWrapper> iterator = selectedUsers.iterator();
        while (iterator.hasNext()) {
            MMXUserWrapper next = iterator.next();
            if (next.isSelected()) users.add(next.getObj());
        }

        if (!users.isEmpty()) {
            if (mmxChannel != null) {
                mmxChannel.addSubscribers(new HashSet<>(users), new MMXChannel.OnFinishedListener<List<String>>() {
                    @Override
                    public void onSuccess(List<String> strings) {
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                        toast(R.string.err_users_channel);
                    }
                });
            } else {
                Intent intent = MMXChatActivity.createIntent(this, users);

                if (intent == null) {
                    showMessage("Can't open chat. The list of users is empty!");
                    return;
                }

                finish();
                startActivity(intent);
            }
        }
    }
}
