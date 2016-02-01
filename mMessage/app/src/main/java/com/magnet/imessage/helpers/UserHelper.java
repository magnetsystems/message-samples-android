package com.magnet.imessage.helpers;

import com.magnet.imessage.core.CurrentApplication;
import com.magnet.imessage.preferences.UserPreference;
import com.magnet.imessage.util.Logger;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.Max;
import com.magnet.max.android.User;
import com.magnet.max.android.auth.model.UserRegistrationInfo;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.internal.channel.UserInfo;

import java.util.List;

public class UserHelper {

    private static UserHelper instance;

    private UserHelper() {

    }

    public interface OnLoginListener {
        void onSuccess();
        void onFailedLogin(ApiError apiError);
    }

    public interface OnLogoutListener {
        void onSuccess();
        void onFailedLogin(ApiError apiError);
    }

    public interface OnRegisterListener extends OnLoginListener {
        void onFailedRegistration(ApiError apiError);
    }

    public static UserHelper getInstance() {
        if (instance == null) {
            instance = new UserHelper();
        }
        return instance;
    }

    public void registerUser(String firstName, String lastName, final String email, final String password, final OnRegisterListener onRegisterListener) {
        UserRegistrationInfo.Builder infoBuilder = new UserRegistrationInfo.Builder();
        infoBuilder.firstName(firstName).lastName(lastName).email(email).userName(email).password(password);
        User.register(infoBuilder.build(), new ApiCallback<User>() {
            @Override
            public void success(User user) {
                Logger.debug("register", "success");
                login(email, password, true, onRegisterListener);
            }

            @Override
            public void failure(ApiError apiError) {
                Logger.error("register", apiError);
                onRegisterListener.onFailedRegistration(apiError);
            }
        });
    }

    public void relogin(OnLoginListener onLoginListener) {
        String[] credence = UserPreference.getInstance().readCredence();
        if (User.getCurrentUser() == null && credence != null) {
            login(credence[0], credence[1], false, onLoginListener);
        }
    }

    public void login(final String email, final String password, final boolean remember, final OnLoginListener onLoginListener) {
        User.login(email, password, remember, new ApiCallback<Boolean>() {
            @Override
            public void success(Boolean aBoolean) {
                Max.initModule(MMX.getModule(), new ApiCallback<Boolean>() {
                    @Override
                    public void success(Boolean aBoolean) {
                        if (remember) {
                            UserPreference.getInstance().saveCredence(email, password);
                        }
                        CurrentApplication.getInstance().setLogined(true);
                        MMX.start();
                        Logger.debug("login", "success");
                        onLoginListener.onSuccess();
                    }

                    @Override
                    public void failure(ApiError apiError) {
                        Logger.error("init module", apiError);
                        onLoginListener.onFailedLogin(apiError);
                    }
                });
            }

            @Override
            public void failure(ApiError apiError) {
                Logger.error("login", apiError);
                onLoginListener.onFailedLogin(apiError);
            }
        });
    }

    public void logout(final OnLogoutListener listener) {
        User.logout(new ApiCallback<Boolean>() {
            @Override
            public void success(Boolean aBoolean) {
                UserPreference.getInstance().cleanCredence();
                CurrentApplication.getInstance().setConversations(null);
                Logger.debug("logout", "success");
                listener.onSuccess();
            }

            @Override
            public void failure(ApiError apiError) {
                Logger.error("logout", apiError);
                listener.onFailedLogin(apiError);
            }
        });
    }

    public String userNamesAsString(List<UserInfo> userList) {
        String users = "";
        for (int i = 0; i < userList.size(); i++) {
            UserInfo user = userList.get(i);
            users += user.getDisplayName();
            if (i != userList.size() - 1) {
                users += ", ";
            }
        }
        return users;
    }

}
