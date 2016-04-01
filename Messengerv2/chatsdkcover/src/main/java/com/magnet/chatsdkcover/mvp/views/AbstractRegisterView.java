package com.magnet.chatsdkcover.mvp.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.magnet.chatsdkcover.R;
import com.magnet.chatsdkcover.mvp.abs.BasePresenterView;
import com.magnet.chatsdkcover.mvp.api.abs.RegisterContract;
import com.magnet.magnetchat.helpers.UserHelper;

//import com.magnet.magntetchatapp.R;

//import butterknife.InjectView;

/**
 * Created by dlernatovich on 3/15/16.
 */
public abstract class AbstractRegisterView extends BasePresenterView<RegisterContract.Presenter> implements RegisterContract.View {

    AppCompatEditText editFirstName;
    AppCompatEditText editLastName;
    AppCompatEditText editEmail;
    AppCompatEditText editPassword;
    AppCompatEditText editPasswordAgain;

    AppCompatTextView labelEnterName;
    AppCompatTextView labelEnterEmail;
    AppCompatTextView labelEnterPassword;

    AppCompatButton buttonRegister;

    View viewProgress;

    ViewGroup viewProgressInside;
    AppCompatTextView labelLoading;
    ProgressBar progressLoading;

    //ATTRIBUTES
    private Drawable backgroundEditFirstName;
    private Drawable backgroundEditLastName;
    private Drawable backgroundEditEmail;
    private Drawable backgroundEditPassword;
    private Drawable backgroundEditPasswordAgain;
    private Drawable backgroundButton;

    private ColorStateList colorTextEdits;
    private ColorStateList colorHintEdits;
    private ColorStateList colorTextLabels;
    private ColorStateList colorTextButtons;

    private ColorStateList colorTextLoading;
    private ColorStateList colorBackgroundLoading;

    private int dimenLabelsText;
    private int dimenEditsText;
    private int dimenButtonsText;

    private int minimumPasswordLength;

    //VARIABLES
    private RegisterContract.OnRegisterActionCallback registerActionCallback;

    public AbstractRegisterView(Context context) {
        super(context);
    }

    public AbstractRegisterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractRegisterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Method which provide the attribute initializing
     *
     * @param attrs attributes
     */
    @Override
    protected void onAttributeInitialize(@NonNull AttributeSet attrs) {
        TypedArray attributes = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.AbstractRegisterView,
                0, 0);
        try {
            backgroundEditFirstName = attributes.getDrawable(R.styleable.AbstractRegisterView_backgroundRegEdits);
            backgroundEditLastName = attributes.getDrawable(R.styleable.AbstractRegisterView_backgroundRegEdits);
            backgroundEditEmail = attributes.getDrawable(R.styleable.AbstractRegisterView_backgroundRegEdits);
            backgroundEditPassword = attributes.getDrawable(R.styleable.AbstractRegisterView_backgroundRegEdits);
            backgroundEditPasswordAgain = attributes.getDrawable(R.styleable.AbstractRegisterView_backgroundRegEdits);
            backgroundButton = attributes.getDrawable(R.styleable.AbstractRegisterView_backgroundRegButton);

            colorTextEdits = attributes.getColorStateList(R.styleable.AbstractRegisterView_colorRegEdits);
            colorHintEdits = attributes.getColorStateList(R.styleable.AbstractRegisterView_colorRegHintEdits);
            colorTextLabels = attributes.getColorStateList(R.styleable.AbstractRegisterView_colorRegLabels);
            colorTextButtons = attributes.getColorStateList(R.styleable.AbstractRegisterView_colorRegTextButton);

            colorTextLoading = attributes.getColorStateList(R.styleable.AbstractRegisterView_colorRegLoadingMessage);
            colorBackgroundLoading = attributes.getColorStateList(R.styleable.AbstractRegisterView_colorRegLoadingBackground);

            dimenLabelsText = attributes.getDimensionPixelSize(R.styleable.AbstractRegisterView_dimenRegLabels, R.dimen.text_18);
            dimenEditsText = attributes.getDimensionPixelSize(R.styleable.AbstractRegisterView_dimenRegEdits, R.dimen.text_18);
            dimenButtonsText = attributes.getDimensionPixelSize(R.styleable.AbstractRegisterView_dimenRegButton, R.dimen.text_18);

            minimumPasswordLength = attributes.getInt(R.styleable.AbstractRegisterView_minimumRegPassLength, 6);
        } finally {
            attributes.recycle();
            onApplyAttributes();
        }
    }

    /**
     * Method which provide the UI customizing with accordance to the custom attributes
     */
    protected void onApplyAttributes() {

        if (backgroundEditFirstName != null) {
            editFirstName.setBackgroundDrawable(backgroundEditFirstName);
        }

        if (backgroundEditLastName != null) {
            editLastName.setBackgroundDrawable(backgroundEditLastName);
        }

        if (backgroundEditEmail != null) {
            editEmail.setBackgroundDrawable(backgroundEditEmail);
        }

        if (backgroundEditPassword != null) {
            editPassword.setBackgroundDrawable(backgroundEditPassword);
        }

        if (backgroundEditPasswordAgain != null) {
            editPasswordAgain.setBackgroundDrawable(backgroundEditPasswordAgain);
        }

        if (backgroundButton != null) {
            buttonRegister.setBackgroundDrawable(backgroundButton);
        }

        if (colorTextEdits != null) {
            editFirstName.setTextColor(colorTextEdits);
            editLastName.setTextColor(colorTextEdits);
            editEmail.setTextColor(colorTextEdits);
            editPassword.setTextColor(colorTextEdits);
            editPasswordAgain.setTextColor(colorTextEdits);
        }

        if (colorHintEdits != null) {
            editFirstName.setHintTextColor(colorHintEdits);
            editLastName.setHintTextColor(colorHintEdits);
            editEmail.setHintTextColor(colorHintEdits);
            editPassword.setHintTextColor(colorHintEdits);
            editPasswordAgain.setHintTextColor(colorHintEdits);
        }

        if (colorTextLabels != null) {
            labelEnterName.setTextColor(colorTextLabels);
            labelEnterEmail.setTextColor(colorTextLabels);
            labelEnterPassword.setTextColor(colorTextLabels);
        }

        if (colorTextButtons != null) {
            buttonRegister.setTextColor(colorTextButtons);
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

        editFirstName.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimenEditsText);
        editLastName.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimenEditsText);
        editEmail.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimenEditsText);
        editPassword.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimenEditsText);
        editPasswordAgain.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimenEditsText);
        labelEnterName.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimenLabelsText);
        labelEnterEmail.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimenLabelsText);
        labelEnterPassword.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimenLabelsText);
        buttonRegister.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimenButtonsText);
    }

    /**
     * Method which provide to getting of the layout ID
     *
     * @return layout ID
     */
    @Override
    protected int getLayoutId() {
        return R.layout.view_register_cover;
    }

    @Override
    protected void onLinkInterface() {
        editFirstName = (AppCompatEditText) findViewById(R.id.editFirstName);
        editLastName = (AppCompatEditText) findViewById(R.id.editLastName);
        editEmail = (AppCompatEditText) findViewById(R.id.editEmail);
        editPassword = (AppCompatEditText) findViewById(R.id.editPassword);
        editPasswordAgain = (AppCompatEditText) findViewById(R.id.editPasswordAgain);

        labelEnterName = (AppCompatTextView) findViewById(R.id.labelEnterName);
        labelEnterEmail = (AppCompatTextView) findViewById(R.id.labelEnterEmail);
        labelEnterPassword = (AppCompatTextView) findViewById(R.id.labelEnterPassword);

        buttonRegister = (AppCompatButton) findViewById(R.id.buttonRegister);

        viewProgress = findViewById(R.id.viewProgress);

        labelLoading = (AppCompatTextView) findViewById(R.id.labelLoading);
        progressLoading = (ProgressBar) findViewById(R.id.progressLoading);
        viewProgressInside = (ViewGroup) findViewById(R.id.viewProgressInside);

    }

    /**
     * Method which provide the action when view will create
     */
    @Override
    protected void onCreateView() {
        setOnClickListeners(buttonRegister);
    }

    /**
     * Method which provide to getting of the context inside the View/Activity/Fragment
     *
     * @return current view
     */
    @NonNull
    @Override
    public Context getCurrentContext() {
        return getContext();
    }

    /**
     * Method which provide the action for onClickListener
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonRegister) {
            presenter.startRegister();
        }
    }

    //SETTERS

    /**
     * Method which provide to setting of the OnRegisterActionCallback
     *
     * @param registerActionCallback current callback
     */
    public void setRegisterActionCallback(RegisterContract.OnRegisterActionCallback registerActionCallback) {
        this.registerActionCallback = registerActionCallback;
    }

    /**
     * Method which provide to getting of the OnRegisterActionCallback from the view
     *
     * @return current callback
     */
    @Nullable
    @Override
    public RegisterContract.OnRegisterActionCallback getRegisterCallback() {
        return registerActionCallback;
    }

    /**
     * Method which provide the fields verifying
     *
     * @return checking result
     */
    @Override
    public boolean verifyFields() {
        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String passwordAgain = editPasswordAgain.getText().toString().trim();

        if (firstName == null || firstName.isEmpty() == true) {
            editFirstName.setError("First name shouldn't be empty");
            editFirstName.requestFocus();
            return false;
        }

        if (lastName == null || lastName.isEmpty() == true) {
            editLastName.setError("Last name shouldn't be empty");
            editLastName.requestFocus();
            return false;
        }

        if (email == null || email.isEmpty() == true) {
            editEmail.setError("Email shouldn't be empty");
            editEmail.requestFocus();
            return false;
        } else {
            if (UserHelper.isEmail(email) == false) {
                editEmail.setText("");
                editEmail.setError("Invalid email format");
                editEmail.requestFocus();
                return false;
            }
        }


        if (password == null || password.isEmpty() == true) {
            editPassword.setError("Password shouldn't be empty");
            editPassword.requestFocus();
            return false;
        }

        if (password.length() < minimumPasswordLength) {
            editPassword.setText("");
            editPasswordAgain.setText("");
            editPassword.setError(String.format("Passwords is too short, should be at least %d characters", minimumPasswordLength));
            editPassword.requestFocus();
            return false;
        }

        if (passwordAgain == null || passwordAgain.isEmpty() == true) {
            editPasswordAgain.setError("Repeated password shouldn't be empty");
            editPasswordAgain.requestFocus();
            return false;
        }


        if (password.equals(passwordAgain) == false) {
            editPassword.setText("");
            editPasswordAgain.setText("");
            editPassword.setError("Passwords mismatch");
            editPassword.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Method which provide the getting of the first name
     *
     * @return current first name
     */
    @NonNull
    @Override
    public String getFirstName() {
        return editFirstName.getText().toString().trim();
    }

    /**
     * Method which provide the getting of the last name
     *
     * @return current last name
     */
    @NonNull
    @Override
    public String getLastName() {
        return editLastName.getText().toString().trim();
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
     * Method which provide the getting of the email
     *
     * @return current email
     */
    @NonNull
    @Override
    public String getEmail() {
        return editEmail.getText().toString().trim();
    }

    /**
     * Method which provide the progress switching
     *
     * @param isNeedProgress is need progress
     */
    @Override
    public void switchProgress(boolean isNeedProgress) {
        if (isNeedProgress == true) {
            viewProgress.setVisibility(VISIBLE);
        } else {
            viewProgress.setVisibility(GONE);
        }
    }

    //EXAMPLE TO CUSTOMIZE

//    <com.magnet.chatsdkcover.ui.views.section.register.DefaultRegisterView
//    android:id="@+id/viewRegister"
//    android:layout_width="match_parent"
//    android:layout_height="wrap_content"
//    android:layout_marginLeft="@dimen/dimen_16"
//    android:layout_marginRight="@dimen/dimen_16"
//    app:backgroundRegEdits="@drawable/background_edit_login"
//    app:colorRegEdits="@android:color/black"
//    app:colorRegHintEdits="@android:color/darker_gray"
//    app:colorRegLabels="@android:color/black"
//    app:colorRegLoadingBackground="@color/colorBlue"
//    app:colorRegLoadingMessage="@android:color/white"
//    app:colorRegTextButton="@color/colorBlueDark"
//    app:dimenRegButton="@dimen/text_14"
//    app:dimenRegEdits="@dimen/text_14"
//    app:dimenRegLabels="@dimen/text_14"
//    app:minimumRegPassLength="6" />

}
