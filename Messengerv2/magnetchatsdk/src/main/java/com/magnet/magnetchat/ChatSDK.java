package com.magnet.magnetchat;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

import com.magnet.magnetchat.core.managers.ChatManager;
import com.magnet.magnetchat.core.managers.InternetConnectionManager;
import com.magnet.magnetchat.core.managers.SharedPreferenceManager;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.model.converters.factories.MMXObjectConverterFactory;
import com.magnet.magnetchat.model.converters.impl.DefaultMMXObjectConverterFactory;
import com.magnet.magnetchat.presenters.core.PresenterFactory;
import com.magnet.magnetchat.presenters.impl.DefaultPresenterFactory;
import com.magnet.magnetchat.ui.factories.DefaultMMXViewFactory;
import com.magnet.magnetchat.ui.factories.MMXViewFactory;
import com.magnet.magnetchat.util.Logger;
import com.magnet.max.android.*;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.common.Log;

/**
 * Class which provide to management of the MMX functional
 */
public class ChatSDK {

    private PresenterFactory factory;
    private MMXViewFactory mmxViewFactory;
    private MMXObjectConverterFactory mmxObjectConverterFactory;

    private static ChatSDK instance;

    private ChatSDK() {

    }

    public MMXObjectConverterFactory getObjectConverterFactory() {
        if (mmxObjectConverterFactory == null) {
            mmxObjectConverterFactory = new DefaultMMXObjectConverterFactory();
        }
        return mmxObjectConverterFactory;
    }

    private MMXViewFactory getMmxViewFactory() {
        if (mmxViewFactory == null) {
            mmxViewFactory = new DefaultMMXViewFactory();
        }
        return mmxViewFactory;
    }

    private PresenterFactory getFactory() {
        if (factory == null) {
            factory = new DefaultPresenterFactory();
        }
        return factory;
    }

    public static MMXObjectConverterFactory getMmxObjectConverterFactory() {
        throwMMXNotInitExcetion();
        return instance.getObjectConverterFactory();
    }

    public static PresenterFactory getPresenterFactory() {
        throwMMXNotInitExcetion();
        return instance.getFactory();
    }

    public static MMXViewFactory getViewFactory() {
        return instance.getMmxViewFactory();
    }

    private static void throwMMXNotInitExcetion() {
        if (instance == null) {
            throw new RuntimeException("ChatSDK wasn't initialized. Call ChatSDK.init method in Application class");
        }
    }

    public static void init(Application application) {
        instance = new ChatSDK();

        MMX.registerListener(eventListener);
        MMX.registerWakeupBroadcast(application, new Intent("MMX_WAKEUP_ACTION"));

        Log.setLoggable(null, BuildConfig.DEBUG ? Log.VERBOSE : Log.ERROR);

        SharedPreferenceManager.getInstance(application);
        InternetConnectionManager.getInstance(application);
        ChatManager.getInstance();
    }

    public static void messageNotification(String channelName, String fromUserName) {
        PendingIntent intent = PendingIntent.getActivity(Max.getApplicationContext(), 0, new Intent(Intent.ACTION_MAIN)
                        .addCategory(Intent.CATEGORY_DEFAULT)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .setPackage(Max.getApplicationContext().getPackageName()),
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(Max.getApplicationContext())
                .setAutoCancel(true)
                .setSmallIcon(Max.getApplicationContext().getApplicationInfo().icon)
                .setContentTitle("New message is available")
                .setContentInfo(fromUserName)
                .setContentIntent(intent).build();
        NotificationManager manager = (NotificationManager) Max.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(channelName, 12345, notification);
        Vibrator v = (Vibrator) Max.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }

    private static MMX.EventListener eventListener = new MMX.EventListener() {
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
            ChatManager.getInstance().handleIncomingMessage(mmxMessage, null);
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
            ChatManager.getInstance().approveMessage(messageId);
            return false;
        }
    };
}
