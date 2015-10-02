package com.magnet.smartshopper;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;

import com.magnet.mmx.client.MMXClient;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.api.MMXUser;
import com.magnet.mmx.client.common.Log;
import com.magnet.mmx.client.common.MMXid;


public class SmartShopperApp extends Application {

    private static final String KEY_MESSAGE_TEXT = "textContent";
    private static final String TAG = "SmartShopperApp";
    private int mNoteId = 0;

    private static SmartShopperApp instance;

    private MMX.EventListener mListener =
            new MMX.EventListener() {
                public boolean onMessageReceived(MMXMessage mmxMessage) {

                    Log.d(TAG, "Message received from " + mmxMessage.getSender().getDisplayName() +
                            " message =" + mmxMessage.getContent().toString());
                    return false;
                }

                public boolean onMessageAcknowledgementReceived(MMXid mmXid, String s) {
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
        Object textObj = message.getContent().get(KEY_MESSAGE_TEXT);
        if (textObj != null) {
            String messageText = textObj.toString();
            MMXUser from = message.getSender();
            NotificationManager noteMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification note = new Notification.Builder(this).setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_launcher).setWhen(System.currentTimeMillis())
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentTitle("Message from " + from.getUsername()).setContentText(messageText).build();
            noteMgr.notify(mNoteId++, note);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        MMX.init(this, R.raw.demoapp);
        com.magnet.mmx.client.common.Log.setLoggable(null, com.magnet.mmx.client.common.Log.VERBOSE);
        MMXClient.registerWakeupListener(this, MyWakeupListener.class);
        MMX.registerListener(mListener);
    }

    public static SmartShopperApp getInstance() {
        return instance;
    }
}