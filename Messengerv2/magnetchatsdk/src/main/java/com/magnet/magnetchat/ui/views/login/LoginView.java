package com.magnet.magnetchat.ui.views.login;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.core.managers.InternetConnectionManager;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.helpers.UserInterfaceHelper;
import com.magnet.magnetchat.ui.views.abs.BaseView;
import com.magnet.magnetchat.util.Logger;
import com.magnet.max.android.ApiError;
import com.magnet.mmx.client.api.MMX;

import java.net.SocketTimeoutException;

/**
 * Created by dlernatovich on 2/29/16.
 */
@Deprecated
public class LoginView extends BaseView<LoginViewProperties> {

    private ImageView imageLogo;
    private AppCompatTextView labelAdditionalInformation;
    private AppCompatEditText editEmail;
    private AppCompatEditText editPassword;
    private AppCompatCheckBox checkBoxRememberMe;
    private AppCompatTextView buttonSignIn;
    private AppCompatTextView buttonCreateAccount;
    private View viewProgress;
    private OnLoginEventListener onLoginEventListener;

    public LoginView(Context context) {
        super(context);
    }

    public LoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoginView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_login;
    }

    @Override
    protected void onLinkingViews(View baseView) {
        imageLogo = (ImageView) baseView.findViewById(R.id.image_logo);
        labelAdditionalInformation = (AppCompatTextView) baseView.findViewById(R.id.labelAdditionalInformation);
        editEmail = (AppCompatEditText) baseView.findViewById(R.id.loginEmail);
        editPassword = (AppCompatEditText) baseView.findViewById(R.id.loginPassword);
        checkBoxRememberMe = (AppCompatCheckBox) baseView.findViewById(R.id.loginRemember);
        buttonSignIn = (AppCompatTextView) baseView.findViewById(R.id.loginSignInBtn);
        buttonCreateAccount = (AppCompatTextView) baseView.findViewById(R.id.loginCreateAccountBtn);
        viewProgress = (View) baseView.findViewById(R.id.viewProgress);
    }

    @Override
    protected void onCreateView() {
        setOnClickListeners(buttonSignIn, buttonCreateAccount);
        viewProgress.setVisibility(GONE);
    }


    @Override
    public void onResume() {
        super.onResume();
        //if (User.getCurrentUser() != null) {
        //    switchLoginProgressView(true);
        //} else {
        //    switchLoginProgressView(false);
        //}
    }

    @Override
    public void onClick(View v) {
        hideKeyboard();
        if (v.getId() == R.id.loginCreateAccountBtn) {
            if (onLoginEventListener != null) {
                onLoginEventListener.onRegisterPressed();
            }
        } else if (v.getId() == R.id.loginSignInBtn) {
            startLogIn();
        }
    }

    /**
     * Method which provide the running log in functional
     */
    private void startLogIn() {
        if (InternetConnectionManager.getInstance().isAnyConnectionAvailable()) {
            final String email = UserInterfaceHelper.getStringFromField(editEmail);
            final String password = UserInterfaceHelper.getStringFromField(editPassword);
            boolean shouldRemember = checkBoxRememberMe.isChecked();
            if (checkStrings(email, password)) {
                switchLoginProgressView(true);
                UserHelper.login(email, password, shouldRemember, loginListener);
            } else {
                showLoginFailed();
            }
        } else {
            showNoConnection();
        }
    }

    /**
     * Method which provide to send of the incorrect password message
     */
    private void showLoginFailed() {
        if (onLoginEventListener != null) {
            onLoginEventListener.onLoginError(null, "Email or password is incorrect");
        }
        switchLoginProgressView(false);
    }

    /**
     * Method which provide to send error message with description
     *
     * @param cause
     */
    private void showLoginErrorCause(String cause) {
        if (onLoginEventListener != null) {
            onLoginEventListener.onLoginError(null, cause);
        }
        switchLoginProgressView(false);
    }

    /**
     * Method which provide to send the error message about no connection
     */
    private void showNoConnection() {
        if (onLoginEventListener != null) {
            onLoginEventListener.onLoginError(null, "No connection");
        }
        switchLoginProgressView(false);
    }

    /**
     * Method which provide to show/hide login view
     *
     * @param runLogining is need show/hide
     */
    private void switchLoginProgressView(boolean runLogining) {
        if (runLogining == true) {
            viewProgress.setVisibility(View.VISIBLE);
        } else {
            viewProgress.setVisibility(View.GONE);
        }
    }

    /**
     * Listener which provide the watchdog for the logining functional
     */
    private final UserHelper.OnLoginListener loginListener = new UserHelper.OnLoginListener() {
        @Override
        public void onSuccess() {
            if (onLoginEventListener != null) {
                onLoginEventListener.onLoginPerformed();
            }
        }

        @Override
        public void onFailedLogin(ApiError apiError) {
            Logger.error("login", apiError);
            switchLoginProgressView(false);
            if (apiError.getMessage().contains(MMX.FailureCode.BAD_REQUEST.getDescription())) {
                showLoginErrorCause("A bad request submitted to the server.");
            } else if (apiError.getMessage().contains(MMX.FailureCode.SERVER_AUTH_FAILED.getDescription())) {
                showLoginErrorCause("Server authentication failure.");
            } else if (apiError.getMessage().contains(MMX.FailureCode.DEVICE_CONCURRENT_LOGIN.getDescription())) {
                showLoginErrorCause("Concurrent logins are attempted.");
            } else if (apiError.getMessage().contains(MMX.FailureCode.DEVICE_ERROR.getDescription())) {
                showLoginErrorCause("A client error.");
            } else if (apiError.getMessage().contains(MMX.FailureCode.SERVER_ERROR.getDescription())) {
                showLoginErrorCause("A server error.");
            } else if (apiError.getMessage().contains(MMX.FailureCode.SERVICE_UNAVAILABLE.getDescription())) {
                showLoginErrorCause("Service is not available due to network or server issue.");
            } else if (null != apiError.getCause() && apiError.getCause() instanceof SocketTimeoutException) {
                showLoginErrorCause("Request timeout. Please check network.");
            } else {
                showLoginFailed();
            }
        }
    };

    //GETTERS

    public ImageView getImageLogo() {
        return imageLogo;
    }

    public AppCompatTextView getLabelAdditionalInformation() {
        return labelAdditionalInformation;
    }

    public AppCompatEditText getEditEmail() {
        return editEmail;
    }

    public AppCompatEditText getEditPassword() {
        return editPassword;
    }

    public AppCompatCheckBox getCheckBoxRememberMe() {
        return checkBoxRememberMe;
    }

    public AppCompatTextView getButtonSignIn() {
        return buttonSignIn;
    }

    //SETTERS

    public void setOnLoginEventListener(OnLoginEventListener onLoginEventListener) {
        this.onLoginEventListener = onLoginEventListener;
    }


    //CALLBACKS

    public interface OnLoginEventListener {
        enum Event {
            LOGIN,
            REGISTER,
            LOGIN_ERROR
        }

        void onLoginPerformed();

        void onRegisterPressed();

        void onLoginError(ApiError apiError, String message);

    }

    //PROPERTIES

//    @Override
//    public void setProperties(LoginViewProperties property) {
//        if (property != null) {
//
//            //Typeface
//            if (property.getTypeface() != null) {
//                labelAdditionalInformation.setTypeface(property.getTypeface());
//                editEmail.setTypeface(property.getTypeface());
//                editPassword.setTypeface(property.getTypeface());
//                checkBoxRememberMe.setTypeface(property.getTypeface());
//                buttonSignIn.setTypeface(property.getTypeface());
//                buttonCreateAccount.setTypeface(property.getTypeface());
//            }
//
//        }
//    }
}
