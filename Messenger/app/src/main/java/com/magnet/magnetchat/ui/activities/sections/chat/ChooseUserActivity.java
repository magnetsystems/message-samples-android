package com.magnet.magnetchat.ui.activities.sections.chat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.core.application.CurrentApplication;
import com.magnet.magnetchat.core.managers.ChannelCacheManager;
import com.magnet.magnetchat.helpers.ChannelHelper;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.ui.activities.abs.BaseActivity;
import com.magnet.magnetchat.ui.adapters.SelectedUsersAdapter;
import com.magnet.magnetchat.ui.adapters.UsersAdapter;
import com.magnet.magnetchat.ui.custom.CustomSearchView;
import com.magnet.magnetchat.util.Logger;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.InjectView;

public class ChooseUserActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    public static final String TAG_ADD_USER_TO_CHANNEL = "addUserToChannel";

    private final String SEARCH_QUERY = "firstName:%s* OR lastName:%s*";

    private enum ActivityMode {MODE_TO_CREATE, MODE_TO_ADD_USER}

    @InjectView(R.id.chooseUserList)
    RecyclerView userList;
    @InjectView(R.id.selectedUserList)
    RecyclerView selectedUserList;
    @InjectView(R.id.tvSelectedUsersAmount)
    TextView tvSelectedAmount;
    @InjectView(R.id.llSelectedUsers)
    LinearLayout llSelectedUsers;
    @InjectView(R.id.chooseUserProgress)
    ProgressBar userSearchProgress;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    private UsersAdapter adapter;
    private SelectedUsersAdapter selectedAdapter;
    private ActivityMode currentMode;
    private Conversation conversation;
    private List<UserProfile> selectedUsers;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_choose_user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setOnClickListeners(R.id.registerSaveBtn);

        userList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        selectedUserList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        selectedUsers = new ArrayList<>();
        selectedAdapter = new SelectedUsersAdapter(this, selectedUsers);
        selectedUserList.setAdapter(selectedAdapter);

        searchUsers("");
        currentMode = ActivityMode.MODE_TO_CREATE;
        String channelName = getIntent().getStringExtra(TAG_ADD_USER_TO_CHANNEL);
        if (channelName != null) {
            conversation = ChannelCacheManager.getInstance().getConversationByName(channelName);
            currentMode = ActivityMode.MODE_TO_ADD_USER;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setTitle("All contacts");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerSaveBtn:
                onAddUserPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_choose_user, menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final CustomSearchView search = (CustomSearchView) menu.findItem(R.id.menuUserSearch).getActionView();
            search.setHint("Search users");
            search.setOnQueryTextListener(this);
            search.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    search.onActionViewCollapsed();
                    hideKeyboard();
                    searchUsers("");
                    return true;
                }
            });
            search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        hideKeyboard();
                    }
                }
            });
        }
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
        if (newText.isEmpty()) {
            hideKeyboard();
            searchUsers("");
        }
        return true;
    }

    /**
     * Method which provide to create channel or add user
     */
    private void onAddUserPressed() {
        if (selectedUsers.size() > 0) {
            switch (currentMode) {
                case MODE_TO_ADD_USER:
                    addUserToChannel(selectedUsers);
                    break;
                case MODE_TO_CREATE:
                    String[] userIds = new String[selectedUsers.size()];
                    for (int i = 0; i < userIds.length; i++) {
                        userIds[i] = selectedUsers.get(i).getUserIdentifier();
                    }
                    startActivity(ChatActivity.getIntentForNewChannel(userIds));
                    finish();
                    break;
            }
        } else {
            showMessage("Nobody was selected");
        }
    }

    private void addUserToChannel(final List<UserProfile> userList) {
        userSearchProgress.setVisibility(View.VISIBLE);
        ChannelHelper.addUserToConversation(conversation, userList, new ChannelHelper.OnAddUserListener() {
            @Override
            public void onSuccessAdded() {
                userSearchProgress.setVisibility(View.INVISIBLE);
                finish();
            }

            @Override
            public void onUserSetExists(String channelSetName) {
                userSearchProgress.setVisibility(View.INVISIBLE);
                Conversation anotherConversation = ChannelCacheManager.getInstance().getConversationByName(channelSetName);
                Intent i = ChatActivity.getIntentWithChannel(anotherConversation);
                if (null != i) {
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onWasAlreadyAdded() {
                userSearchProgress.setVisibility(View.INVISIBLE);
                showMessage("User was already added");
                finish();

            }

            @Override
            public void onFailure(Throwable throwable) {
                userSearchProgress.setVisibility(View.INVISIBLE);
                showMessage("Can't add user to channel");
            }
        });
    }

    private void searchUsers(@NonNull String query) {
        userSearchProgress.setVisibility(View.VISIBLE);
        User.search(String.format(SEARCH_QUERY, query, query), 100, 0, "lastName:asc", new ApiCallback<List<User>>() {
            @Override
            public void success(List<User> users) {
                users.remove(User.getCurrentUser());
                if (conversation != null) {
                    for (UserProfile user : conversation.getSuppliersList()) {
                        users.remove(user);
                    }
                }
                Collections.sort(users, UserHelper.getUserProfileComparator());
                userSearchProgress.setVisibility(View.INVISIBLE);
                Logger.debug("find users", "success");
                updateList(users);
            }

            @Override
            public void failure(ApiError apiError) {
                userSearchProgress.setVisibility(View.INVISIBLE);
                showMessage("Can't find users");
                Logger.error("find users", apiError);
            }
        });
    }

    private void updateList(List<? extends UserProfile> users) {
        adapter = new UsersAdapter(this, users, selectedUsers);
        userList.setAdapter(adapter);
        adapter.setOnUserClickListener(new UsersAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(UserProfile user, int position) {
                hideKeyboard();
                if (user != null) {
                    if (selectedUsers.contains(user)) {
                        selectedUsers.remove(user);
                    } else {
                        selectedUsers.add(user);
                    }
                    if (selectedUsers.size() > 0){
                        tvSelectedAmount.setText(String.format("%d selected", selectedUsers.size()));
                        llSelectedUsers.setVisibility(View.VISIBLE);
                    } else {
                        llSelectedUsers.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                    selectedAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public static Intent getIntentToCreateChannel() {
        return new Intent(CurrentApplication.getInstance(), ChooseUserActivity.class);
    }

    public static Intent getIntentToAddUserToChannel(String channelName) {
        Intent intent = new Intent(CurrentApplication.getInstance(), ChooseUserActivity.class);
        intent.putExtra(TAG_ADD_USER_TO_CHANNEL, channelName);
        return intent;
    }

}
