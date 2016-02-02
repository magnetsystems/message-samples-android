package com.magnet.magnetchat.helpers;

import com.magnet.magnetchat.core.ConversationCache;
import com.magnet.magnetchat.preferences.UserPreference;
import com.magnet.magnetchat.util.Logger;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;
import com.magnet.max.android.auth.model.UserRegistrationInfo;
import com.magnet.max.android.util.StringUtil;
import com.magnet.mmx.client.api.MMX;

import java.util.List;

public class UserHelper {

    private static final String EMAIL_TEMPLATE = "^[-a-z0-9!#$%&'*+/=?^_`{|}~]+(?:\\.[-a-z0-9!#$%&'*+/=?^_`{|}~]+)*@(?:[a-z0-9]([-a-z0-9]{0,61}[a-z0-9])?\\.)*(?:aero|arpa|asia|biz|cat|com|coop|edu|gov|info|int|jobs|mil|mobi|museum|name|net|org|pro|tel|travel|[a-z][a-z])$";

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
                login(email, password, false, onRegisterListener);
            }

            @Override
            public void failure(ApiError apiError) {
                Logger.error("register", apiError);
                if (onRegisterListener != null)
                    onRegisterListener.onFailedRegistration(apiError);
            }
        });
    }

    public void login(final String email, final String password, final boolean remember, final OnLoginListener onLoginListener) {
        User.login(email, password, remember, new ApiCallback<Boolean>() {
            @Override
            public void success(Boolean aBoolean) {
                if (remember) {
                    UserPreference.getInstance().saveCredence(email, password);
                }
                MMX.start();
                Logger.debug("login", "success");
                if (onLoginListener != null)
                    onLoginListener.onSuccess();
            }

            @Override
            public void failure(ApiError apiError) {
                Logger.error("login", apiError);
                if (onLoginListener != null)
                    onLoginListener.onFailedLogin(apiError);
            }
        });
    }

    public void checkAuthentication(OnLoginListener onLoginListener) {
        if (User.getCurrentUser() == null) {
            String[] credence = UserPreference.getInstance().readCredence();
            if (credence != null) {
                login(credence[0], credence[1], true, onLoginListener);
            }
        }
    }

    public void logout(final OnLogoutListener listener) {
        User.logout(new ApiCallback<Boolean>() {
            @Override
            public void success(Boolean aBoolean) {
                ConversationCache.getInstance().resetConversations();
                Logger.debug("logout", "success");
                if (listener != null)
                    listener.onSuccess();
            }

            @Override
            public void failure(ApiError apiError) {
                Logger.error("logout", apiError);
                if (listener != null)
                    listener.onFailedLogin(apiError);
            }
        });
    }

    public String userNameAsString(UserProfile user) {
        return StringUtil.isNotEmpty(user.getDisplayName()) ? user.getDisplayName() : String.format("%s %s", user.getFirstName(), user.getLastName());
    }

    public String userNamesAsString(List<UserProfile> userList) {
        String users = "";
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i) != null) {
                users += userNameAsString(userList.get(i));
                if (i != userList.size() - 1) {
                    users += ", ";
                }
            }
        }
        return users;
    }

    public static boolean checkEmail(String email) {
        return email.matches(EMAIL_TEMPLATE);
    }

}
