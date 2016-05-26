package com.magnet.magnetchat.ui.views.login;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.presenters.LoginContract;
import com.magnet.magnetchat.ui.views.abs.BasePresenterView;

/**
 * Created by dlernatovich on 3/11/16.
 */
public class DefaultLoginView extends AbstractLoginView {

    AppCompatEditText editEmail;
    AppCompatEditText editPassword;
    AppCompatCheckBox checkBoxRemember;
    AppCompatButton buttonLogin;
    AppCompatButton buttonRegister;
    View viewProgress;

    ViewGroup viewProgressInside;
    AppCompatTextView labelLoading;
    ProgressBar progressLoading;

    //ATTRIBUTES
    private Drawable backgroundsEdit;
    private ColorStateList textColorEdits;
    private ColorStateList textHintColorEdits;
    private String hintEmail;
    private String hintPassword;
    private boolean isNeedRemember;
    private String textRememberCheckBox;
    private ColorStateList colorButtonRemember;
    private ColorStateList colorTextRemember;
    private Drawable backgroundButtonLogin;
    private Drawable backgroundButtonEdit;
    private ColorStateList colorTextButtons;
    private String textButtonLogin;
    private String textButtonRegister;
    private int dimenEditsText;
    private int dimenRememberText;
    private int dimenButtonsText;
    private ColorStateList colorTextLoading;
    private ColorStateList colorBackgroundLoading;
    private String factory_name;

    public DefaultLoginView(Context context) {
        super(context);
    }

    public DefaultLoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultLoginView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Method which provide the interface linking
     */
    @Override
    protected void onLinkInterface() {
        editEmail = (AppCompatEditText) findViewById(R.id.editEmail);
        editPassword = (AppCompatEditText) findViewById(R.id.editPassword);
        checkBoxRemember = (AppCompatCheckBox) findViewById(R.id.checkBoxRememberMe);
        buttonLogin = (AppCompatButton) findViewById(R.id.buttonLogin);
        buttonRegister = (AppCompatButton) findViewById(R.id.buttonRegister);
        viewProgress = findViewById(R.id.viewProgress);

        labelLoading = (AppCompatTextView) findViewById(R.id.labelLoading);
        progressLoading = (ProgressBar) findViewById(R.id.progressLoading);
        viewProgressInside = (ViewGroup) findViewById(R.id.viewProgressInside);
    }

    @Override
    protected void onCreateView() {
        super.onCreateView();
        setOnClickListeners(buttonLogin, buttonRegister);
    }

    @Override
    public void onClick(View v) {
        hideKeyboard();
        if (v.getId() == R.id.buttonRegister) {
            LoginContract.OnLoginActionCallback loginActionCallback = getLoginActionCallback();
            if (loginActionCallback != null) {
                loginActionCallback.onRegisterPressed();
            }
        } else if (v.getId() == R.id.buttonLogin) {
            startLogin();
        }
    }

    /**
     * Method which provide the attribute initializing
     *
     * @param attrs attributes
     */
    @Override
    protected void onAttributeInitialize(@NonNull AttributeSet attrs) {
        TypedArray attributes = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.DefaultLoginView, 0, 0);
        try {
            isNeedRemember = attributes.getBoolean(R.styleable.DefaultLoginView_isNeedRemember, true);
            textColorEdits = attributes.getColorStateList(R.styleable.DefaultLoginView_textColorEdits);
            textHintColorEdits = attributes.getColorStateList(R.styleable.DefaultLoginView_textHintColorEdits);
            colorButtonRemember = attributes.getColorStateList(R.styleable.DefaultLoginView_colorButtonRemember);
            colorTextRemember = attributes.getColorStateList(R.styleable.DefaultLoginView_colorTextRemember);
            backgroundsEdit = attributes.getDrawable(R.styleable.DefaultLoginView_backgroundsEdit);
            hintEmail = attributes.getString(R.styleable.DefaultLoginView_hintEmail);
            hintPassword = attributes.getString(R.styleable.DefaultLoginView_hintPassword);
            textRememberCheckBox = attributes.getString(R.styleable.DefaultLoginView_textRemember);
            backgroundButtonLogin = attributes.getDrawable(R.styleable.DefaultLoginView_backgroundsButtons);
            backgroundButtonEdit = attributes.getDrawable(R.styleable.DefaultLoginView_backgroundsButtons);
            colorTextButtons = attributes.getColorStateList(R.styleable.DefaultLoginView_colorTextButtons);
            textButtonLogin = attributes.getString(R.styleable.DefaultLoginView_textButtonLogin);
            textButtonRegister = attributes.getString(R.styleable.DefaultLoginView_textButtonRegister);
            dimenEditsText = attributes.getDimensionPixelSize(R.styleable.DefaultLoginView_dimenEditsText, R.dimen.text_18);
            dimenRememberText = attributes.getDimensionPixelSize(R.styleable.DefaultLoginView_dimenRememberText, R.dimen.text_18);
            dimenButtonsText = attributes.getDimensionPixelSize(R.styleable.DefaultLoginView_dimenButtonsText, R.dimen.text_18);

            colorTextLoading = attributes.getColorStateList(R.styleable.DefaultLoginView_colorLogLoadingMessage);
            colorBackgroundLoading = attributes.getColorStateList(R.styleable.DefaultLoginView_colorLogLoadingBackground);

            factory_name = attributes.getString(R.styleable.DefaultLoginView_colorLogLoadingBackground);
        } finally {
            attributes.recycle();
            onApplyAttributes();
        }
    }


    /**
     * Method which provide to getting of the layout ID
     *
     * @return layout ID
     */
    @Override
    protected int getLayoutId() {
        return R.layout.view_login_cover;
    }


    /**
     * Method which provide the UI customizing with accordance to the custom attributes
     */
    protected void onApplyAttributes() {
        if (backgroundsEdit != null) {
            editEmail.setBackgroundDrawable(backgroundsEdit);
            editPassword.setBackgroundDrawable(backgroundsEdit);
        }
        if (textColorEdits != null) {
            editEmail.setTextColor(textColorEdits);
            editPassword.setTextColor(textColorEdits);
        }
        if (textHintColorEdits != null) {
            editEmail.setHintTextColor(textHintColorEdits);
            editPassword.setHintTextColor(textHintColorEdits);
        }
        if (hintEmail != null && hintEmail.isEmpty() != true) {
            editEmail.setHint(hintEmail);
        }
        if (hintPassword != null && hintPassword.isEmpty() != true) {
            editPassword.setHint(hintPassword);
        }
        if (isNeedRemember == true) {
            checkBoxRemember.setVisibility(VISIBLE);
        } else {
            checkBoxRemember.setVisibility(GONE);
        }
        if (textRememberCheckBox != null && textRememberCheckBox.isEmpty() != true) {
            checkBoxRemember.setText(textRememberCheckBox);
        }
        if (colorButtonRemember != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                checkBoxRemember.setButtonTintList(colorButtonRemember);
            } else {
                checkBoxRemember.setSupportButtonTintList(colorButtonRemember);
            }
        }
        if (colorTextRemember != null) {
            checkBoxRemember.setTextColor(colorTextRemember);
        }
        if (backgroundButtonLogin != null) {
            buttonLogin.setBackground(backgroundButtonLogin);
        }
        if (backgroundButtonEdit != null) {
            buttonRegister.setBackground(backgroundButtonEdit);
        }
        if (colorTextButtons != null) {
            buttonLogin.setTextColor(colorTextButtons);
            buttonRegister.setTextColor(colorTextButtons);
        }
        if (textButtonLogin != null && textButtonLogin.isEmpty() != true) {
            buttonLogin.setText(textButtonLogin);
        }
        if (textButtonRegister != null && textButtonRegister.isEmpty() != true) {
            buttonRegister.setText(textButtonRegister);
        }

        if (colorBackgroundLoading != null) {
            viewProgressInside.getBackground().setColorFilter(colorBackgroundLoading
                    .getColorForState(EMPTY_STATE_SET, android.R.color.transparent), PorterDuff.Mode.SRC_IN);
        }

        if (colorTextLoading != null) {
            labelLoading.setTextColor(colorTextLoading);
            progressLoading.getIndeterminateDrawable().setColorFilter(colorTextLoading
                    .getColorForState(EMPTY_STATE_SET, android.R.color.transparent), PorterDuff.Mode.SRC_IN);
        }

        editEmail.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimenEditsText);
        editPassword.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimenEditsText);
        checkBoxRemember.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimenRememberText);
        buttonLogin.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimenButtonsText);
        buttonRegister.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimenButtonsText);
    }

    protected String getPresenterName() {
        return factory_name;
    }

    /**
     * Method which provide the field verifying
     *
     * @return checking results
     */
    @Override
    public boolean verifyFields() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (email.isEmpty() == true) {
            editEmail.setError("Email shouldn't be empty");
            editEmail.requestFocus();
            return false;
        }

        if (password.isEmpty() == true) {
            editPassword.setError("Password shouldn't be empty");
            editPassword.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Method which provide to show/hide pregress view
     *
     * @param visible
     */
    @Override
    public void switchProgress(boolean visible) {
        if (visible == true) {
            viewProgress.setVisibility(VISIBLE);
        } else {
            viewProgress.setVisibility(GONE);
        }
    }

    /**
     * Method which provide to getting of the user name (email)
     *
     * @return email
     */
    @NonNull
    @Override
    public String getEmail() {
        return editEmail.getText().toString().trim();
    }

    /**
     * Method which provide the getting of the password
     *
     * @return current password
     */
    @NonNull
    @Override
    public String getPassword() {
        return editPassword.getText().toString().trim();
    }

    /**
     * Method which provide to getting of the value if user should be remember
     *
     * @return getting value
     */
    @Override
    public boolean getShouldRemember() {
        return checkBoxRemember.isChecked();
    }



    //EXAMPLES:
    //CUSTOMIZATION THROUGH THE XML
//    <com.magnet.magnetchat.ui.views.section.LoginView
//    android:id="@+id/viewLogin"
//    android:layout_width="match_parent"
//    android:layout_height="wrap_content"
//    android:layout_marginLeft="@dimen/dimen_15"
//    android:layout_marginRight="@dimen/dimen_15"
//    android:layout_marginTop="@dimen/dimen_10"
//    app:backgroundsEdit="@drawable/background_edit_login"
//    app:colorButtonRemember="@color/colorBlueDark"
//    app:colorTextButtons="@color/colorBlueDark"
//    app:colorTextRemember="@android:color/black"
//    app:dimenButtonsText="@dimen/text_14"
//    app:dimenEditsText="@dimen/text_14"
//    app:dimenRememberText="@dimen/text_14"
//    app:hintEmail="Enter email here"
//    app:hintPassword="Enter password here"
//    app:textButtonLogin="Sign in"
//    app:textButtonRegister="Create account"
//    app:textColorEdits="@android:color/black"
//    app:textHintColorEdits="@android:color/darker_gray"
//    app:textRemember="Remember me" />
}
