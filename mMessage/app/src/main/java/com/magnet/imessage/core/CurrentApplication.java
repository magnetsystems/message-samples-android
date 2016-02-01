package com.magnet.imessage.core;

import android.app.Application;
import android.content.Intent;

import com.magnet.imessage.R;
import com.magnet.imessage.model.Conversation;
import com.magnet.imessage.preferences.UserPreference;
import com.magnet.imessage.services.CheckAuthorization;
import com.magnet.max.android.Max;
import com.magnet.max.android.config.MaxAndroidPropertiesConfig;

import java.util.List;

public class CurrentApplication extends Application {

    private static CurrentApplication instance;

    private List<Conversation> conversations;
    private boolean isLogined;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Max.init(this.getApplicationContext(), new MaxAndroidPropertiesConfig(this, R.raw.magnetmax));
        UserPreference.getInstance(this);
        startService(new Intent(this, CheckAuthorization.class));
    }

    public static CurrentApplication getInstance() {
        return instance;
    }

    public List<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
    }

    public Conversation getConversationByIdx(int idx) {
        if (conversations != null) {
            return conversations.get(idx);
        }
        return null;
    }

    public boolean isLogined() {
        return isLogined;
    }

    public void setLogined(boolean isLogined) {
        this.isLogined = isLogined;
    }
}
