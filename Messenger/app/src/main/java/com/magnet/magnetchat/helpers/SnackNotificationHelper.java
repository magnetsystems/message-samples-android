package com.magnet.magnetchat.helpers;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.ui.activities.sections.splash.SplashActivity;

/**
 * Created by dlernatovich on 2/10/16.
 */
public class SnackNotificationHelper {
    /**
     * Method which provide to show the SnackBar
     *
     * @param currentView base view
     * @param message     snack bar message
     */
    public static void show(View currentView, String message) {
        final Snackbar snackbar = Snackbar.make(currentView, message, Snackbar.LENGTH_LONG)
                .setAction("Close", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setActionTextColor(currentView.getResources().getColor(R.color.colorAccent));
        View view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        if (tv != null) {
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentView.getContext().getResources().getDimension(R.dimen.text_14));
        }
        snackbar.show();
    }

  public static void showNotification(Context context, String title, String text, String tag) {
    Log.d("NotificationHelper", "Going to show " + title);
    PendingIntent pIntent =
        PendingIntent.getActivity(context, 999, new Intent(context, SplashActivity.class),
            PendingIntent.FLAG_UPDATE_CURRENT);
    Notification notificationCompat = new NotificationCompat.Builder(context).setAutoCancel(true)
        .setSmallIcon(context.getApplicationInfo().icon)
        .setContentTitle(title)
        .setContentText(text)
        .setContentIntent(pIntent)
        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
        .build();
    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
    notificationManager.notify(tag, 9999, notificationCompat);
  }
}
