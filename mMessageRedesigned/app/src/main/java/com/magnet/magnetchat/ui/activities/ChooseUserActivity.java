package com.magnet.magnetchat.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.core.application.CurrentApplication;
import com.magnet.magnetchat.core.managers.ChannelCacheManager;
import com.magnet.magnetchat.helpers.ChannelHelper;
import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.ui.adapters.UsersAdapter;
import com.magnet.magnetchat.util.Logger;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;

import java.util.List;

public class ChooseUserActivity extends BaseActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, AdapterView.OnItemClickListener {

    public static final String TAG_ADD_USER_TO_CHANNEL = "addUserToChannel";

    private final String SEARCH_QUERY = "firstName:%s* OR lastName:%s*";

    private enum ActivityMode {MODE_TO_CREATE, MODE_TO_ADD_USER}

    private UsersAdapter adapter;
    private ListView userList;
    private ActivityMode currentMode;
    private Conversation conversation;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_choose_user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setOnClickListeners(R.id.registerSaveBtn);

        userList = (ListView) findViewById(R.id.chooseUserList);
        userList.setOnItemClickListener(this);
        SearchView search = (SearchView) findViewById(R.id.chooseUserSearch);
        search.setOnQueryTextListener(this);
        search.setOnCloseListener(this);
        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard();
                }
            }
        });
        searchUsers("");
        currentMode = ActivityMode.MODE_TO_CREATE;
        String channelName = getIntent().getStringExtra(TAG_ADD_USER_TO_CHANNEL);
        if (channelName != null) {
            conversation = ChannelCacheManager.getInstance().getConversationByName(channelName);
            currentMode = ActivityMode.MODE_TO_ADD_USER;
            setTitle("Add Contact");
        } else {
            setTitle("New Message");
        }
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        hideKeyboard();
        adapter.setSelectUser(view, position);
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
        if (adapter != null && adapter.getSelectedUsers().size() > 0) {
            switch (currentMode) {
                case MODE_TO_ADD_USER:
                    addUserToChannel(adapter.getSelectedUsers());
                    break;
                case MODE_TO_CREATE:
                    List<UserProfile> profileList = adapter.getSelectedUsers();
                    String[] userIds = new String[profileList.size()];
                    for (int i = 0; i < userIds.length; i++) {
                        userIds[i] = profileList.get(i).getUserIdentifier();
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
        findViewById(R.id.chooseUserProgress).setVisibility(View.VISIBLE);
        ChannelHelper.getInstance().addUserToConversation(conversation, userList, new ChannelHelper.OnAddUserListener() {
            @Override
            public void onSuccessAdded() {
                findViewById(R.id.chooseUserProgress).setVisibility(View.INVISIBLE);
                finish();
            }

            @Override
            public void onUserSetExists(String channelSetName) {
                findViewById(R.id.chooseUserProgress).setVisibility(View.INVISIBLE);
                Conversation anotherConversation = ChannelCacheManager.getInstance().getConversationByName(channelSetName);
                startActivity(ChatActivity.getIntentWithChannel(anotherConversation));
                finish();
            }

            @Override
            public void onWasAlreadyAdded() {
                findViewById(R.id.chooseUserProgress).setVisibility(View.INVISIBLE);
                showMessage("User was already added");
                finish();

            }

            @Override
            public void onFailure(Throwable throwable) {
                findViewById(R.id.chooseUserProgress).setVisibility(View.INVISIBLE);
                showMessage("Can't add user to channel");
            }
        });
    }

    private void searchUsers(@NonNull String query) {
        findViewById(R.id.chooseUserProgress).setVisibility(View.VISIBLE);
        User.search(String.format(SEARCH_QUERY, query, query), 100, 0, "lastName:asc", new ApiCallback<List<User>>() {
            @Override
            public void success(List<User> users) {
                users.remove(User.getCurrentUser());
                if (conversation != null) {
                    for (UserProfile user : conversation.getSuppliersList()) {
                        users.remove(user);
                    }
                }
                findViewById(R.id.chooseUserProgress).setVisibility(View.INVISIBLE);
                Logger.debug("find users", "success");
                updateList(users);
            }

            @Override
            public void failure(ApiError apiError) {
                findViewById(R.id.chooseUserProgress).setVisibility(View.INVISIBLE);
                showMessage("Can't find users");
                Logger.error("find users", apiError);
            }
        });
    }

    private void updateList(List<? extends UserProfile> users) {
        adapter = new UsersAdapter(this, users);
        userList.setAdapter(adapter);
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
