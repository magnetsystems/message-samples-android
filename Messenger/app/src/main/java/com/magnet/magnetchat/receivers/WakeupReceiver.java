package com.magnet.magnetchat.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import com.magnet.magnetchat.helpers.SnackNotificationHelper;
import com.magnet.magnetchat.ui.activities.sections.login.LoginActivity;
import com.magnet.magnetchat.ui.activities.sections.splash.SplashActivity;
import com.magnet.max.android.util.StringUtil;
import com.magnet.mmx.client.api.MMXPushEvent;
import com.magnet.mmx.protocol.PubSubNotification;

public class WakeupReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        MMXPushEvent event = MMXPushEvent.fromIntent(intent);
        if (event == null) {

        } else if ("retrieve".equals(event.getType())) {
            showNotification(context, "Magnet Messenger", "New message is available");
        } else if (PubSubNotification.getType().equals(event.getType())) {
            PubSubNotification pubsub = event.getCustomObject(PubSubNotification.class);
            showNotification(context, StringUtil.isNotEmpty(pubsub.getTitle()) ? pubsub.getTitle() : "Magnet Messenger",
                StringUtil.isNotEmpty(pubsub.getBody()) ? pubsub.getBody() : "New message is available");
        }
    }

    private void showNotification(Context context, String title, String text) {
        SnackNotificationHelper.showNotification(context, title, text, "wakeup");
        //PendingIntent pIntent = PendingIntent.getActivity(context, 999,
        //        new Intent(context, SplashActivity.class),
        //        PendingIntent.FLAG_UPDATE_CURRENT);
        //Notification notificationCompat = new NotificationCompat.Builder(context)
        //    .setAutoCancel(true)
        //    .setSmallIcon(context.getApplicationInfo().icon)
        //    .setContentTitle(title)
        //    .setContentText(text)
        //    .setContentIntent(pIntent)
        //    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
        //    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
        //    .build();
        //NotificationManagerCompat notificationManager =
        //    NotificationManagerCompat.from(context);
        //notificationManager.notify(9999, notificationCompat);
        //Notification note = new Notification.Builder(context)
        //        .setAutoCancel(true)
        //        .setSmallIcon(context.getApplicationInfo().icon)
        //        .setContentTitle(title)
        //        .setContentText(text)
        //        .setContentIntent(pIntent)
        //        .build();
        //NotificationManager noteMgr = (NotificationManager)
        //        context.getSystemService(Context.NOTIFICATION_SERVICE);
        //noteMgr.notify(9999, note);
    }

}
