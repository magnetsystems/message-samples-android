package com.magnet.magntetchatapp.ui.activities.section.login;

import android.support.annotation.NonNull;

import com.magnet.magntetchatapp.R;
import com.magnet.magntetchatapp.mvp.api.LoginContract;
import com.magnet.magntetchatapp.ui.activities.abs.BaseActivity;
import com.magnet.magntetchatapp.ui.activities.section.edit.EditProfileActivity;
import com.magnet.magntetchatapp.ui.activities.section.register.RegisterActivity;
import com.magnet.magntetchatapp.ui.views.section.login.DefaultLoginView;

import butterknife.InjectView;

public class LoginActivity extends BaseActivity {

    @InjectView(R.id.viewLogin)
    DefaultLoginView loginView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getMenuId() {
        return NONE_MENU;
    }

    @Override
    protected void onCreateActivity() {
        loginView.setLoginActionCallback(loginActionCallback);
        loginView.onCreateActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginView.onResumeActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        loginView.onPauseActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginView.onDestroyActivity();
    }

    /**
     * Callback which provide the action when some actions happened i the login view
     */
    private final LoginContract.OnLoginActionCallback loginActionCallback = new LoginContract.OnLoginActionCallback() {
        @Override
        public void onLoginSuccess() {
            startActivityWithClearTop(EditProfileActivity.class);
        }

        @Override
        public void onLoginError(@NonNull String errorMessage) {
            showMessage(errorMessage);
        }

        @Override
        public void onRegisterPressed() {
            startActivity(RegisterActivity.class, false);
        }
    };
}
