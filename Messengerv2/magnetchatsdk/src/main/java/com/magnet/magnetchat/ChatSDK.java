package com.magnet.magnetchat;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;

import com.magnet.magnetchat.beans.DefaultMMXBeanFactory;
import com.magnet.magnetchat.beans.MMXBeanFactory;
import com.magnet.magnetchat.core.managers.ChatManager;
import com.magnet.magnetchat.core.managers.InternetConnectionManager;
import com.magnet.magnetchat.core.managers.SharedPreferenceManager;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.model.converters.factories.MMXObjectConverterFactory;
import com.magnet.magnetchat.model.converters.impl.DefaultMMXObjectConverterFactory;
import com.magnet.magnetchat.presenters.chatlist.MMXMessagePresenterFactory;
import com.magnet.magnetchat.presenters.core.MMXPresenterFactory;
import com.magnet.magnetchat.presenters.impl.DefaultMMXPresenterFactory;
import com.magnet.magnetchat.ui.factories.DefaultMMXListItemFactory;
import com.magnet.magnetchat.ui.factories.DefaultMMXViewFactory;
import com.magnet.magnetchat.ui.factories.MMXListItemFactory;
import com.magnet.magnetchat.ui.factories.MMXViewFactory;
import com.magnet.magnetchat.util.Logger;
import com.magnet.max.android.*;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.common.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Class which provide to management of the MMX functional
 */
public class ChatSDK {

    private MMXPresenterFactory mmxPresenterFactory;
    private MMXMessagePresenterFactory messagePresenterFactory;
    private MMXViewFactory mmxViewFactory;
    private MMXObjectConverterFactory mmxObjectConverterFactory;
    private MMXListItemFactory mmxListItemFactory;
    private MMXBeanFactory mmxBeanFactory;

//    private Map<String, MMXPresenterFactory> mmxNamedPresenterFactories = new HashMap<>();
//    private Map<String, MMXMessagePresenterFactory> mmxNamedMessagePresenterFactories = new HashMap<>();
//    private Map<String, MMXViewFactory> mmxNameViewFactories = new HashMap<>();
//    private Map<String, MMXObjectConverterFactory> mmxNamedObjectConverterFactories = new HashMap<>();
//    private Map<String, MMXListItemFactory> mmxNamedListItemFactories = new HashMap<>();

    private Map<String, Object> namedFactories = new HashMap<>();

    private static ChatSDK instance;

    private ChatSDK() {

    }

    private MMXListItemFactory getPrMmxListItemFactory() {
        if (mmxListItemFactory == null) {
            mmxListItemFactory = new DefaultMMXListItemFactory();
        }
        return mmxListItemFactory;
    }


    private MMXObjectConverterFactory getObjectConverterFactory() {
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

    private MMXPresenterFactory getMmxPresenterFactory() {
        if (mmxPresenterFactory == null) {
            mmxPresenterFactory = new DefaultMMXPresenterFactory();
        }
        return mmxPresenterFactory;
    }

    private MMXMessagePresenterFactory getMessagePresenterFactory() {
        if (messagePresenterFactory == null) {
            messagePresenterFactory = new DefaultMMXPresenterFactory();
        }
        return messagePresenterFactory;
    }

    private MMXBeanFactory getMmxBeanFactory() {
        if (mmxBeanFactory == null) {
            mmxBeanFactory = new DefaultMMXBeanFactory();
        }
        return mmxBeanFactory;
    }

    public Object getFactoryByName(String name) {
        return namedFactories.get(name);
    }

    public static <T> T getMMXFactotyByName(String name) {
        throwMMXNotInitException();
        Object byName = instance.getFactoryByName(name);
        if (byName != null) {
            try {
                return (T) byName;
            } catch (ClassCastException ex) {
                Logger.debug(ChatSDK.class.getSimpleName(), ex);
                return null;
            }
        }
        return null;
    }

    public static MMXMessagePresenterFactory getMMXMessagPresenterFactory() {
        throwMMXNotInitException();
        return instance.getMessagePresenterFactory();
    }

    public static MMXListItemFactory getMmxListItemFactory() {
        throwMMXNotInitException();
        return instance.getPrMmxListItemFactory();
    }

    public static MMXObjectConverterFactory getMmxObjectConverterFactory() {
        throwMMXNotInitException();
        return instance.getObjectConverterFactory();
    }

    public static MMXPresenterFactory getPresenterFactory() {
        throwMMXNotInitException();
        return instance.getMmxPresenterFactory();
    }

    public static MMXViewFactory getViewFactory() {
        throwMMXNotInitException();
        return instance.getMmxViewFactory();
    }

    public static MMXBeanFactory getMMXBeanFactory() {
        throwMMXNotInitException();
        return instance.getMmxBeanFactory();
    }

    private static void throwMMXNotInitException() {
        if (instance == null) {
            throw new RuntimeException("ChatSDK wasn't initialized. Call ChatSDK.init method in Application class");
        }
    }

    public static void init(Application application) {
        init(new ChatSDK(), application);
    }

    private static void init(ChatSDK chatSDK, Application application) {
        instance = chatSDK;

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

    public static class Builder {
        private ChatSDK sdk;

        public Builder() {
            sdk = new ChatSDK();
        }


        public Builder setDefaultMMXPresenterFactory(MMXPresenterFactory mmxPresenterFactory) {
            sdk.mmxPresenterFactory = mmxPresenterFactory;
            return this;
        }

        public Builder setDefaultMMXMessagePresenterFactory(MMXMessagePresenterFactory messagePresenterFactory) {
            sdk.messagePresenterFactory = messagePresenterFactory;
            return this;
        }

        public Builder setDefaultMMXViewFactory(MMXViewFactory mmxViewFactory) {
            sdk.mmxViewFactory = mmxViewFactory;
            return this;
        }

        public Builder setDefaultMMXObjectConverterFactory(MMXObjectConverterFactory mmxObjectConverterFactory) {
            sdk.mmxObjectConverterFactory = mmxObjectConverterFactory;
            return this;
        }

        public Builder setDefaultMMXListItemFactory(MMXListItemFactory mmxListItemFactory) {
            sdk.mmxListItemFactory = mmxListItemFactory;
            return this;
        }

        public Builder setMMXBeanFactory(MMXBeanFactory beanFactory) {
            sdk.mmxBeanFactory = beanFactory;
            return this;
        }

        public Builder registerNamedPresenterFactory(String key, MMXPresenterFactory factory) {
            if (key == null || key.isEmpty()) {
                throw new IllegalArgumentException("Key cannot be empty or null");
            }

            sdk.namedFactories.put(key, factory);
            return this;
        }

        public void init(Application application) {
            ChatSDK.init(sdk, application);
        }
    }

}
