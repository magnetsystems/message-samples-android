package com.magnet.messagingsample;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import com.magnet.max.android.Max;
import com.magnet.max.android.User;
import com.magnet.max.android.config.MaxAndroidPropertiesConfig;
import com.magnet.messagingsample.activities.ChatActivity;
import com.magnet.mmx.client.MMXClient;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXMessage;

/**
 * Created by edwardyang on 9/10/15.
 */
public class ChatApplication extends Application {

    private static Context context;
    private int mNoteId = 0;

    private MMX.EventListener mListener = new MMX.EventListener() {
        public boolean onMessageReceived(MMXMessage mmxMessage) {
            doNotify(mmxMessage);
            return false;
        }

        public boolean onMessageAcknowledgementReceived(User from, String messageId) {
            return false;
        }
    };

    public class MyWakeupListener implements MMXClient.MMXWakeupListener {
        public void onWakeupReceived(final Context applicationContext, Intent intent) {
            MMX.registerListener(new MMX.EventListener() {
                public boolean onMessageReceived(MMXMessage mmxMessage) {
                    doNotify(mmxMessage);
                    return false;
                }
            });
        }
    }

    private void doNotify(com.magnet.mmx.client.api.MMXMessage message) {
        Object textObj = message.getContent().get(ChatActivity.KEY_MESSAGE_TEXT);
        if (textObj != null) {
            String messageText = textObj.toString();
            User from = message.getSender();
            NotificationManager noteMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification note = new Notification.Builder(this).setAutoCancel(true)
                    .setSmallIcon(R.drawable.bubble_green).setWhen(System.currentTimeMillis())
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentTitle("Message from " + from.getUserName()).setContentText(messageText).build();
            noteMgr.notify(mNoteId++, note);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ChatApplication.context = this;

//        MMX.init(this, R.raw.messagingsample);
        Max.init(this.getApplicationContext(), new MaxAndroidPropertiesConfig(this, R.raw.magnetmax));
        MMXClient.registerWakeupListener(this, MyWakeupListener.class);
        MMX.registerListener(mListener);
    }

}
