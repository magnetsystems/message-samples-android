package com.magnet.magnetchat.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.ChannelHelper;
import com.magnet.magnetchat.helpers.InternetConnection;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.util.Logger;
import com.magnet.max.android.Max;
import com.magnet.max.android.User;
import com.magnet.max.android.config.MaxAndroidPropertiesConfig;
import com.magnet.max.android.util.StringUtil;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;
import io.fabric.sdk.android.Fabric;

public class CurrentApplication extends MultiDexApplication {

    private static CurrentApplication instance;

    private Notification notification;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        instance = this;
        Max.init(this.getApplicationContext(), new MaxAndroidPropertiesConfig(this, R.raw.magnetmax));
        InternetConnection.getInstance(this);
        com.magnet.mmx.client.common.Log.setLoggable(null, com.magnet.mmx.client.common.Log.VERBOSE);
        MMX.registerListener(eventListener);
        MMX.registerWakeupBroadcast(this, new Intent("MMX_WAKEUP_ACTION"));
    }

    //Enable MultiDex
    public void attachBaseContext(Context base) {
        MultiDex.install(base);
        super.attachBaseContext(base);
    }

    public static CurrentApplication getInstance() {
        return instance;
    }

    public void messageNotification(String channelName, String fromUserName) {
        if (notification == null) {
            PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(Intent.ACTION_MAIN)
                            .addCategory(Intent.CATEGORY_DEFAULT)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .setPackage(this.getPackageName()),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            notification = new Notification.Builder(this).setAutoCancel(true).setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Magnet Messenger")
                    .setContentText((StringUtil.isNotEmpty(fromUserName) ? "New message from " + fromUserName : "New message is available"))
                    //.setContentInfo(fromUserName)
                    .setContentIntent(intent).build();
        }

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(channelName, 12345, notification);
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }

    private MMX.EventListener eventListener = new MMX.EventListener() {
        @Override
        public boolean onLoginRequired(MMX.LoginReason reason) {
            Logger.debug("login required", reason.name());
            UserHelper.getInstance().checkAuthentication(null);
            return false;
        }

        @Override
        public boolean onInviteReceived(MMXChannel.MMXInvite invite) {
            Logger.debug("invite to", invite.getInviteInfo().getChannel().getName());
            return super.onInviteReceived(invite);
        }

        @Override
        public boolean onMessageReceived(MMXMessage mmxMessage) {
            ChannelHelper.getInstance().receiveMessage(mmxMessage);
            if (mmxMessage.getSender() != null && !mmxMessage.getSender().getUserIdentifier().equals(
                User.getCurrentUserId())) {
                messageNotification(null != mmxMessage.getChannel() ? mmxMessage.getChannel().getName() : "", mmxMessage.getSender().getDisplayName());
            }
            return false;
        }

        @Override
        public boolean onMessageAcknowledgementReceived(User from, String messageId) {
            ConversationCache.getInstance().approveMessage(messageId);
            return false;
        }
    };
}
