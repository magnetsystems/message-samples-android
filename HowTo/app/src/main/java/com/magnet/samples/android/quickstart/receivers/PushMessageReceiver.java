/*
 *  Copyright (c) 2016 Magnet Systems, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.magnet.samples.android.quickstart.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import com.magnet.mmx.client.api.MMXPushEvent;
import com.magnet.samples.android.quickstart.util.Logger;
import java.util.Map;

public class PushMessageReceiver extends BroadcastReceiver {

    private Uri alarmSound;

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.debug("push message", "received");
        MMXPushEvent event = MMXPushEvent.fromIntent(intent);
        if (event != null) {
            Map<String, Object> messageContent = event.getCustomMap();
            Object messageText = messageContent.get("content");
            Object messageSoundOn = messageContent.get("soundOn");
            if (messageText != null) {
                boolean soundOn = false;
                if (messageSoundOn != null) {
                    soundOn = (Boolean) messageSoundOn;
                }
                showNotification(context, "New message is available", messageText.toString(), soundOn);
            }
        }
    }

    private void showNotification(Context context, String title, String text, boolean playSound) {
        PendingIntent pIntent = PendingIntent.getActivity(context, 0,
                new Intent(Intent.ACTION_MAIN)
                        .addCategory(Intent.CATEGORY_DEFAULT)
                        .addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
                        .setPackage(context.getPackageName()),
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder noteBuilder = new Notification.Builder(context);
        noteBuilder.setAutoCancel(true)
                .setSmallIcon(context.getApplicationInfo().icon)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pIntent);
        if (playSound) {
            noteBuilder.setSound(getAlarmSound());
        }
        NotificationManager noteMgr = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        noteMgr.notify(9999, noteBuilder.build());
    }

    private Uri getAlarmSound() {
        if (alarmSound == null) {
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        return alarmSound;
    }

}
