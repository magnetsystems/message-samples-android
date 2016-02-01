package com.magnet.imessage.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.magnet.imessage.R;
import com.magnet.imessage.core.CurrentApplication;
import com.magnet.imessage.helpers.ChannelHelper;
import com.magnet.imessage.helpers.UserHelper;
import com.magnet.imessage.model.Conversation;
import com.magnet.imessage.ui.adapters.ConversationsAdapter;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {

    private DrawerLayout drawer;
    private String username;
    private ConversationsAdapter adapter;
    private ListView conversationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (User.getCurrentUser() != null) {
            username = String.format("%s %s", User.getCurrentUser().getFirstName(), User.getCurrentUser().getLastName());
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(username);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView navigationName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.homeUserName);
        navigationName.setText(username);

        conversationsList = (ListView) findViewById(R.id.homeConversationsList);
        conversationsList.setOnItemClickListener(this);
        ChannelHelper.getInstance().readConversations(readChannelInfoListener);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Conversation conversation = adapter.getItem(position);
        startActivity(ChatActivity.getIntentWithChannel(conversation));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHomeCreateConversation:
                startActivity(new Intent(this, ChooseUserActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuChangePassword:
                startActivity(new Intent(this, ChangePasswordActivity.class));
                break;
            case R.id.menuSignOut:
                UserHelper.getInstance().logout(logoutListener);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {

    }

//    private void readConversations() {
//        MMXChannel.getAllSubscriptions(new MMXChannel.OnFinishedListener<List<MMXChannel>>() {
//            @Override
//            public void onSuccess(List<MMXChannel> channels) {
//                Logger.debug("read conversations", "success");
//                CurrentApplication.getInstance().setConversations(new ArrayList<Conversation>(channels.size()));
//                for (MMXChannel channel : channels) {
//                    Conversation conversation = new Conversation();
//                    conversation.setChannel(channel);
//                    CurrentApplication.getInstance().getConversations().add(conversation);
//                    ChannelHelper.getInstance().readMessagesToConversation(channel, conversation, readChannelInfoListener);
//                    ChannelHelper.getInstance().readSubscribersToConversation(channel, conversation, readChannelInfoListener);
//                }
//                updateList();
//            }
//
//            @Override
//            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
//                showMessage("Can't read conversations");
//                Logger.error("read conversations", throwable);
//            }
//        });
//    }

    private void updateList() {
        adapter = new ConversationsAdapter(this, CurrentApplication.getInstance().getConversations());
        conversationsList.setAdapter(adapter);
    }

    private ChannelHelper.OnReadChannelInfoListener readChannelInfoListener = new ChannelHelper.OnReadChannelInfoListener() {

        @Override
        public void onSuccessFinish(Conversation lastConversation) {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onFailure(Throwable throwable) {
            showMessage(throwable.getMessage());
        }
    };

    private UserHelper.OnLogoutListener logoutListener = new UserHelper.OnLogoutListener() {
        @Override
        public void onSuccess() {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }

        @Override
        public void onFailedLogin(ApiError apiError) {
            showMessage("Can't sign out");
        }
    };

}
