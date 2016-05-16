package com.magnet.magnetchat.ui.activities;

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
import com.magnet.magnetchat.ui.fragments.AllUserListFragment;
import com.magnet.magnetchat.ui.fragments.UserListFragment;

/**
 * Created by aorehov on 13.05.16.
 */
public class UsersActivity extends BaseActivity {

    private UserListFragment userListFragment;
    private Handler handler = new Handler();
    private String searchText = "";

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

        userListFragment = new AllUserListFragment();
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
        } else
            return super.onOptionsItemSelected(item);
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

}
