package com.magnet.magnetchat.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.magnet.magnetchat.ui.activities.LoginActivity;
import com.magnet.mmx.client.api.MMXPushEvent;
import com.magnet.mmx.protocol.PubSubNotification;

public class WakeupReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        MMXPushEvent event = MMXPushEvent.fromIntent(intent);
        if (event == null) {

        } else if ("retrieve".equals(event.getType())) {
            showNotification(context, "New message is available", null);
        } else if (PubSubNotification.getType().equals(event.getType())) {
            PubSubNotification pubsub = event.getCustomObject(PubSubNotification.class);
            showNotification(context, pubsub.getText(), pubsub.getTitle());
        }
    }

    private void showNotification(Context context, String title, String text) {
        PendingIntent pIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, LoginActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification note = new Notification.Builder(context)
                .setAutoCancel(true)
                .setSmallIcon(context.getApplicationInfo().icon)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pIntent)
                .build();
        NotificationManager noteMgr = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        noteMgr.notify(9999, note);
    }

}