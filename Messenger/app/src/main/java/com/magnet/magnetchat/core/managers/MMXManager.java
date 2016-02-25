package com.magnet.magnetchat.core.managers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.ChannelHelper;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.util.Logger;
import com.magnet.max.android.Max;
import com.magnet.max.android.User;
import com.magnet.max.android.config.MaxAndroidPropertiesConfig;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.lang.ref.WeakReference;

/**
 * Created by dlernatovich on 2/8/16.
 * Class which provide to management of the MMX functional
 */
public class MMXManager {

    private static MMXManager instance;

    private final WeakReference<Context> applicationReference;

    private Notification notification;


    private MMXManager(Context context) {
        //Initialization of the MagnetMax
        Max.init(context, new MaxAndroidPropertiesConfig(context, R.raw.magnetmax));
        MMX.registerListener(eventListener);
        MMX.registerWakeupBroadcast(context, new Intent("MMX_WAKEUP_ACTION"));
        com.magnet.mmx.client.common.Log.setLoggable(null, com.magnet.mmx.client.common.Log.VERBOSE);

        //Catch current application as a WeakReference
        applicationReference = new WeakReference<Context>(context);
    }

    /**
     * Method which provide the initialization of the instance of the MMXManager
     * WARNING: Should be initialized only in the Application singleton
     *
     * @param context application context
     * @return
     * @see com.magnet.magnetchat.core.application.CurrentApplication
     */
    public static MMXManager getInstance(Context context) {
        if (instance == null) {
            instance = new MMXManager(context);
        }
        return instance;
    }

    public static MMXManager getInstance() {
        return instance;
    }

    public void messageNotification(String channelName, String fromUserName) {
        PendingIntent intent = PendingIntent.getActivity(applicationReference.get(), 0, new Intent(Intent.ACTION_MAIN)
                        .addCategory(Intent.CATEGORY_DEFAULT)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .setPackage(applicationReference.get().getPackageName()),
                PendingIntent.FLAG_UPDATE_CURRENT);
        notification = new Notification.Builder(getApplicationContext())
                .setAutoCancel(true)
                .setSmallIcon(getApplicationContext().getApplicationInfo().icon)
                .setContentTitle("New message is available")
                .setContentInfo(fromUserName)
                .setContentIntent(intent).build();
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(channelName, 12345, notification);
        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }

    private MMX.EventListener eventListener = new MMX.EventListener() {
        @Override
        public boolean onLoginRequired(MMX.LoginReason reason) {
            Logger.debug("login required", reason.name());
            UserHelper.checkAuthentication(null);
            return false;
        }

        @Override
        public boolean onInviteReceived(MMXChannel.MMXInvite invite) {
            Logger.debug("invite to", invite.getInviteInfo().getChannel().getName());
            return super.onInviteReceived(invite);
        }

        @Override
        public boolean onMessageReceived(MMXMessage mmxMessage) {
            Logger.debug("onMessageReceived", mmxMessage);
            ChannelHelper.receiveMessage(mmxMessage);
            if ((mmxMessage.getSender() != null)
                    && (!mmxMessage.getSender().getUserIdentifier().equals(User.getCurrentUserId()))) {
                if (mmxMessage.getChannel() != null) {
                    messageNotification(mmxMessage.getChannel().getName(), mmxMessage.getSender().getDisplayName());
                } else {
                    messageNotification("", mmxMessage.getSender().getDisplayName());
                }
            }
            return false;
        }

        @Override
        public boolean onMessageAcknowledgementReceived(User from, String messageId) {
            ChannelCacheManager.getInstance().approveMessage(messageId);
            return false;
        }
    };

    /**
     * Method which provide to getting of the Application context
     *
     * @return current application context
     */
    private Context getApplicationContext() {
        return applicationReference.get();
    }
}
