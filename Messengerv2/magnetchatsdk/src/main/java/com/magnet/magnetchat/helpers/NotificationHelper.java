package com.magnet.magnetchat.helpers;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v7.app.NotificationCompat;

/**
 * Created by dlernatovich on 3/4/16.
 */
public class NotificationHelper {
    private static final int NOTIFICATION_ID = 0x1;

    /**
     * Method which provide the show notification from the activity
     *
     * @param currentActivity  current activity
     * @param anotherActivity  activity which should open when tap on notification
     * @param notificationIcon notification icon
     * @param largeIcon        large notification icon
     * @param title            notification title
     * @param contentText      notification text
     * @param subtext          notification subtext
     */
    public static void showNotification(Activity currentActivity,
                                        Class anotherActivity,
                                        int notificationIcon,
                                        int largeIcon,
                                        String title,
                                        String contentText,
                                        String subtext) {

        //Build the intent
        Intent intent = new Intent(currentActivity, anotherActivity);
        PendingIntent pendingIntent = PendingIntent.getActivity(currentActivity, 0, intent, 0);

        //Create notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(currentActivity);
        builder.setSmallIcon(notificationIcon);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setLargeIcon(BitmapFactory.decodeResource(currentActivity.getResources(), largeIcon));
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        //Set texts to notification
        builder.setContentTitle(title);
        builder.setContentText(contentText);
        builder.setSubText(subtext);

        //Show the notification
        NotificationManager notificationManager = (NotificationManager) currentActivity.getSystemService(
                Activity.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * Method which provide the show notification from the activity
     *
     * @param currentActivity  current activity
     * @param anotherActivity  activity which should open when tap on notification
     * @param notificationIcon notification icon
     * @param title            notification title
     * @param contentText      notification text
     * @param subtext          notification subtext
     */
    public static void showNotification(Activity currentActivity,
                                        Class anotherActivity,
                                        int notificationIcon,
                                        String title,
                                        String contentText,
                                        String subtext) {

        //Build the intent
        Intent intent = new Intent(currentActivity, anotherActivity);
        PendingIntent pendingIntent = PendingIntent.getActivity(currentActivity, 0, intent, 0);

        //Create notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(currentActivity);
        builder.setSmallIcon(notificationIcon);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setLargeIcon(BitmapFactory.decodeResource(currentActivity.getResources(), notificationIcon));
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        //Set texts to notification
        builder.setContentTitle(title);
        builder.setContentText(contentText);
        builder.setSubText(subtext);

        //Show the notification
        NotificationManager notificationManager = (NotificationManager) currentActivity.getSystemService(
                Activity.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * Method which provide the show notification from the activity
     *
     * @param currentActivity current activity
     * @param anotherActivity activity which should open when tap on notification
     * @param title           notification title
     * @param contentText     notification text
     * @param subtext         notification subtext
     */
    public static void showNotification(Activity currentActivity,
                                        Class anotherActivity,
                                        String title,
                                        String contentText,
                                        String subtext) {

        //Get current application icon ID
        int appIcon = currentActivity.getApplication().getApplicationInfo().icon;

        //Build the intent
        Intent intent = new Intent(currentActivity, anotherActivity);
        PendingIntent pendingIntent = PendingIntent.getActivity(currentActivity, 0, intent, 0);

        //Create notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(currentActivity);
        builder.setSmallIcon(appIcon);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setLargeIcon(BitmapFactory.decodeResource(currentActivity.getResources(), appIcon));
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        //Set texts to notification
        builder.setContentTitle(title);
        builder.setContentText(contentText);
        builder.setSubText(subtext);

        //Show the notification
        NotificationManager notificationManager = (NotificationManager) currentActivity.getSystemService(
                Activity.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * Method which provide the show notification from the activity
     *
     * @param currentActivity current activity
     * @param anotherActivity activity which should open when tap on notification
     * @param title           notification title
     * @param contentText     notification text
     */
    public static void showNotification(Activity currentActivity,
                                        Class anotherActivity,
                                        String title,
                                        String contentText) {

        //Get current application icon ID
        int appIcon = currentActivity.getApplication().getApplicationInfo().icon;

        //Build the intent
        Intent intent = new Intent(currentActivity, anotherActivity);
        PendingIntent pendingIntent = PendingIntent.getActivity(currentActivity, 0, intent, 0);

        //Create notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(currentActivity);
        builder.setSmallIcon(appIcon);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setLargeIcon(BitmapFactory.decodeResource(currentActivity.getResources(), appIcon));
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        //Set texts to notification
        builder.setContentTitle(title);
        builder.setContentText(contentText);

        //Show the notification
        NotificationManager notificationManager = (NotificationManager) currentActivity.getSystemService(
                Activity.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * Method which provide the show notification from the activity
     *
     * @param currentActivity current activity
     * @param anotherActivity activity which should open when tap on notification
     * @param title           notification title
     * @param contentText     notification text
     */
    public static void showNotification(Context currentActivity,
                                        Class anotherActivity,
                                        int appIcon,
                                        String title,
                                        String contentText) {

        //Get current application icon ID

        //Build the intent
        Intent intent = new Intent(currentActivity, anotherActivity);
        PendingIntent pendingIntent = PendingIntent.getActivity(currentActivity, 0, intent, 0);

        //Create notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(currentActivity);
        builder.setSmallIcon(appIcon);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setLargeIcon(BitmapFactory.decodeResource(currentActivity.getResources(), appIcon));
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        //Set texts to notification
        builder.setContentTitle(title);
        builder.setContentText(contentText);

        //Show the notification
        NotificationManager notificationManager = (NotificationManager) currentActivity.getSystemService(
                Activity.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * Method which provide the show notification from the activity
     *
     * @param currentActivity current activity
     * @param intent          intent which should open when tap on notification
     * @param title           notification title
     * @param contentText     notification text
     */
    public static void showNotification(Context currentActivity,
                                        Intent intent,
                                        int appIcon,
                                        String title,
                                        String contentText) {

        //Get current application icon ID

        //Build the intent
        PendingIntent pendingIntent = PendingIntent.getActivity(currentActivity, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        //Create notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(currentActivity);
        builder.setSmallIcon(appIcon);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setLargeIcon(BitmapFactory.decodeResource(currentActivity.getResources(), appIcon));
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        //Set texts to notification
        builder.setContentTitle(title);
        builder.setContentText(contentText);

        //Show the notification
        NotificationManager notificationManager = (NotificationManager) currentActivity.getSystemService(
                Activity.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * Method which provide the show notification from the activity
     *
     * @param currentActivity current activity
     * @param intent          intent which should open when tap on notification
     * @param title           notification title
     * @param contentText     notification text
     */
    public static void showNotification(Context currentActivity,
                                        PendingIntent intent,
                                        int appIcon,
                                        String title,
                                        String contentText) {

        //Get current application icon ID

        //Build the intent

        //Create notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(currentActivity);
        builder.setSmallIcon(appIcon);
        builder.setContentIntent(intent);
        builder.setAutoCancel(true);
        builder.setLargeIcon(BitmapFactory.decodeResource(currentActivity.getResources(), appIcon));
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        //Set texts to notification
        builder.setContentTitle(title);
        builder.setContentText(contentText);

        //Show the notification
        NotificationManager notificationManager = (NotificationManager) currentActivity.getSystemService(
                Activity.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
