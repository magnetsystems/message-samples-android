package com.magnet.magntetchatapp.ui.activities.section.register;

import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.magnet.magntetchatapp.R;
import com.magnet.chatsdkcover.mvp.api.RegisterContract;
import com.magnet.chatsdkcover.mvp.views.AbstractRegisterView;
import com.magnet.magntetchatapp.ui.activities.abs.BaseActivity;
import com.magnet.magntetchatapp.ui.activities.section.edit.FirstSetupActivity;

import butterknife.InjectView;

public class RegisterActivity extends BaseActivity {

    @InjectView(R.id.viewRegister)
    AbstractRegisterView registerView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected int getMenuId() {
        return R.menu.menu_register;
    }

    @Override
    protected void onCreateActivity() {
        enableBackButton();
        registerView.setRegisterActionCallback(registerActionCallback);
        registerView.onCreateActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerView.onResumeActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        registerView.onPauseActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerView.onDestroyActivity();
    }

    /**
     * Callback which provide the action when something happened while registering of the new user
     */
    private final RegisterContract.OnRegisterActionCallback registerActionCallback = new RegisterContract.OnRegisterActionCallback() {
        @Override
        public void onRegisterSuccess() {
            startActivityWithClearTop(FirstSetupActivity.class);
        }

        @Override
        public void onRegisterError(@NonNull String message) {
            showMessage(message);
        }
    };
}
