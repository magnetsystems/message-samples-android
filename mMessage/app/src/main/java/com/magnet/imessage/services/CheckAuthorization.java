package com.magnet.imessage.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.magnet.imessage.core.CurrentApplication;
import com.magnet.imessage.helpers.UserHelper;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;

import java.util.Timer;
import java.util.TimerTask;

public class CheckAuthorization extends Service {

    private long second = 1000;
    private long minute = second * 60;
    private long time = 0;

//    public CheckAuthorization() {
//        super("CheckAuthorization");
//    }
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
////        if (User.getCurrentUser() == null) {
////            UserHelper.getInstance().relogin(new UserHelper.OnLoginListener() {
////                @Override
////                public void onSuccess() {
////                }
////
////                @Override
////                public void onFailedLogin(ApiError apiError) {
////                }
////            });
////        }
//
//    }

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (User.getCurrentUser() == null && CurrentApplication.getInstance().isLogined()) {
                UserHelper.getInstance().relogin(new UserHelper.OnLoginListener() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailedLogin(ApiError apiError) {
                    }
                });
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Timer timer = new Timer("TimerToLogin");
        timer.schedule(timerTask, 0, minute * 5);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
