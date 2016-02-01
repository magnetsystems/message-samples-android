package com.magnet.imessage.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.magnet.imessage.R;
import com.magnet.imessage.ui.adapters.UsersAdapter;
import com.magnet.imessage.util.Logger;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;

import java.util.List;

public class ChooseUserActivity extends BaseActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, AdapterView.OnItemClickListener {

    private UsersAdapter adapter;
    private ListView userList;
    private SearchView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);
        userList = (ListView) findViewById(R.id.chooseUserList);
        userList.setOnItemClickListener(this);
        search = (SearchView) findViewById(R.id.chooseUserSearch);
        search.setOnQueryTextListener(this);
        search.setOnCloseListener(this);
        searchUsers("");
        setTitle("New Message");
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        User selectedUser = adapter.getItem(position);
        startActivity(ChatActivity.getIntentForNewChannel(selectedUser.getUserIdentifier()));
    }

    @Override
    public boolean onClose() {
        hideKeyboard();
        searchUsers("");
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        hideKeyboard();
        searchUsers(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void searchUsers(@NonNull String query) {
        findViewById(R.id.chooseUserProgress).setVisibility(View.VISIBLE);
        User.search("lastName:" + query + "*", 100, 0, "lastName:asc", new ApiCallback<List<User>>() {
            @Override
            public void success(List<User> users) {
                users.remove(User.getCurrentUser());
                findViewById(R.id.chooseUserProgress).setVisibility(View.GONE);
                Logger.debug("find users", "success");
                updateList(users);
            }

            @Override
            public void failure(ApiError apiError) {
                findViewById(R.id.chooseUserProgress).setVisibility(View.GONE);
                showMessage("Can't find users");
                Logger.error("find users", apiError);
            }
        });
    }

    private void updateList(List<User> users) {
        adapter = new UsersAdapter(this, users);
        userList.setAdapter(adapter);
    }

}
