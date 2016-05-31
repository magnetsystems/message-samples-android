package com.magnet.magnetchat.helpers;

import com.magnet.magnetchat.core.managers.ChatManager;
import com.magnet.magnetchat.core.managers.SharedPreferenceManager;
import com.magnet.magnetchat.util.Logger;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;
import com.magnet.max.android.auth.model.UserRegistrationInfo;
import com.magnet.max.android.util.StringUtil;
import com.magnet.mmx.client.api.MMX;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Class which provide the user functional
 */
public class UserHelper {

    private static final String EMAIL_TEMPLATE = "^[-a-z0-9!#$%&'*+/=?^_`{|}~]+(?:\\.[-a-z0-9!#$%&'*+/=?^_`{|}~]+)*@(?:[a-z0-9]([-a-z0-9]{0,61}[a-z0-9])?\\.)*(?:aero|arpa|asia|biz|cat|com|coop|edu|gov|info|int|jobs|mil|mobi|museum|name|net|org|pro|tel|travel|[a-z][a-z])$";

    private static final int MAX_USER_NAMES_LENGTH = 22;

    private static final String USER_NAMES_SUFIX = "...";

    private static final String NAME_SEARCH_QUERY = "firstName:%s* OR lastName:%s*";

    /**
     * Login listener
     */
    public interface OnLoginListener {
        /**
         * Method which provide the do some performing when login is successful
         */
        void onSuccess();

        /**
         * Method which provide the do some performing when login is not successful
         */
        void onFailedLogin(ApiError apiError);
    }

    /**
     * Logout listener
     */
    public interface OnLogoutListener {
        /**
         * Method which provide the do some performing when logout is successful
         */
        void onSuccess();

        /**
         * Method which provide the do some performing when logout is not successful
         */
        void onFailedLogin(ApiError apiError);
    }

    public interface OnRegisterListener extends OnLoginListener {
        void onFailedRegistration(ApiError apiError);
    }

    /**
     * Method which provide the user registration
     *
     * @param firstName          user first name
     * @param lastName           user last name
     * @param email              user email
     * @param password           user password
     * @param onRegisterListener on register listener
     */
    public static void register(String firstName, String lastName, final String email, final String password, final OnRegisterListener onRegisterListener) {
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

    /**
     * Method which provide the user authorization
     *
     * @param email           user email
     * @param password        user password
     * @param remember        is need to remember me
     * @param onLoginListener on login listener
     */
    public static void login(final String email, final String password, final boolean remember, final OnLoginListener onLoginListener) {
        User.login(email, password, remember, new ApiCallback<Boolean>() {
            @Override
            public void success(Boolean aBoolean) {
                if (remember) {
                    SharedPreferenceManager.getInstance().saveCredence(email, password);
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


    /**
     * Method which provide the autentification checking
     *
     * @param onLoginListener on login listener
     */
    public static void checkAuthentication(OnLoginListener onLoginListener) {
        if (User.getCurrentUser() == null) {
            Logger.debug("SessionStatus", User.getSessionStatus());
            if (User.SessionStatus.CanResume == User.getSessionStatus()) {
                User.resumeSession(new ApiCallback<Boolean>() {
                    @Override
                    public void success(Boolean aBoolean) {
                        Logger.debug("resume session", "success");
                    }

                    @Override
                    public void failure(ApiError apiError) {
                        Logger.error("resume session", apiError);
                    }
                });
            } else if (User.SessionStatus.CanResume == User.getSessionStatus()) {
                String[] credence = SharedPreferenceManager.getInstance().readCredence();
                if (credence != null) {
                    login(credence[0], credence[1], true, onLoginListener);
                }
            }
        }
    }

    /**
     * Method which provide the logout functional
     *
     * @param listener logout listener
     */
    public static void logout(final OnLogoutListener listener) {
        User.logout(new ApiCallback<Boolean>() {
            @Override
            public void success(Boolean aBoolean) {
                ChatManager.getInstance().resetConversations();
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

    /**
     * Method which provide the getting of the user display name from the UserProfile
     *
     * @param user current user object
     * @return display name
     */
    public static String getDisplayName(UserProfile user) {
        return getDisplayNames(Arrays.asList(user));
    }

    /**
     * Method which provide to getting of the user display names
     *
     * @param userList list of user objects
     * @return users list
     */
    public static String getDisplayNames(List<UserProfile> userList) {
        StringBuilder users = new StringBuilder();
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i) != null) {
                users.append(userList.get(i).getDisplayName());
                if (users.length() < MAX_USER_NAMES_LENGTH - 2 && i != userList.size() - 1) {
                    users.append(", ");
                } else {
                    break;
                }
            }
        }
        return truncateRecipientsName(users.toString());
    }

    public static String truncateRecipientsName(String s) {
        return truncateString(s, MAX_USER_NAMES_LENGTH, USER_NAMES_SUFIX);
    }

    public static String truncateString(String s ,int length, String sufix) {
        if(StringUtil.isNotEmpty(s)) {
            if(s.length() > length) {
                return s.substring(0, length) + sufix;
            }
        }

        return s;
    }

    /**
     * Method which provide the checking if current string is email
     *
     * @param email current email
     * @return checking results
     */
    public static boolean isEmail(String email) {
        return email.matches(EMAIL_TEMPLATE);
    }

    public static String createNameQuery(String term) {
        return String.format(NAME_SEARCH_QUERY, term, term);
    }

    public static Comparator<UserProfile> getUserProfileComparator() {
        return new Comparator<UserProfile>() {
            @Override
            public int compare(UserProfile lhs, UserProfile rhs) {
                if (lhs == null || rhs == null) {
                    return 0;
                }
                String lName = getUserNameToCompare(lhs);
                String rName = getUserNameToCompare(rhs);
                return lName.compareToIgnoreCase(rName);
            }
        };
    }

    public static String getUserNameToCompare(UserProfile userProfile) {
        String str = " ";
        if (userProfile != null) {
            if (userProfile.getLastName() != null) {
                str = userProfile.getLastName();
            } else if (userProfile.getFirstName() != null) {
                str = userProfile.getFirstName();
            } else {
                str = userProfile.getDisplayName();
            }
        }
        if (str.trim().contains(" ")) {
            str = str.substring(str.indexOf(" ")).trim();
        }
        return str;
    }

}
