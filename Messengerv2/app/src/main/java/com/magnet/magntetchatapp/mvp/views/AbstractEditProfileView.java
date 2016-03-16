package com.magnet.magntetchatapp.mvp.views;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.magnet.magnetchat.helpers.FileHelper;
import com.magnet.magntetchatapp.R;
import com.magnet.magntetchatapp.mvp.abs.BasePresenterView;
import com.magnet.magntetchatapp.mvp.api.EditProfileContract;
import com.magnet.max.android.Attachment;
import com.magnet.max.android.User;

import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dlernatovich on 3/16/16.
 */
public abstract class AbstractEditProfileView extends BasePresenterView<EditProfileContract.Presenter> implements EditProfileContract.View {

    public static final int K_IMAGE_PICK_INTENT_CODE = 0x64;
    private static final String TAG = "AbstractEditProfileView";
    private EditProfileContract.OnEditUserCallback editUserCallback;

    @InjectView(R.id.viewEditImage)
    View viewEditImage;
    @InjectView(R.id.imageUser)
    CircleImageView circleImageView;
    @InjectView(R.id.labelEmail)
    AppCompatTextView labelEmail;
    @InjectView(R.id.editFirstName)
    AppCompatEditText editFirstName;
    @InjectView(R.id.editLastName)
    AppCompatEditText editLastName;
    @InjectView(R.id.viewProgress)
    View viewProgress;
    @InjectView(R.id.buttonSaveChanges)
    AppCompatButton buttonSaveChanges;

    //ATTRIBUTES
    private Drawable backgroundEditFirstName;
    private Drawable backgroundEditLastName;
    private Drawable backgroundButton;

    private ColorStateList colorTextEdits;
    private ColorStateList colorHintEdits;
    private ColorStateList colorTextLabels;
    private ColorStateList colorTextButtons;

    private int dimenLabelsText;
    private int dimenEditsText;
    private int dimenButtonsText;

    public AbstractEditProfileView(Context context) {
        super(context);
    }

    public AbstractEditProfileView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractEditProfileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Method which provide the attribute initializing
     *
     * @param attrs attributes
     */
    @Override
    protected void onAttributeInitialize(@NonNull AttributeSet attrs) {
        TypedArray attributes = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.AbstractEditProfileView,
                0, 0);
        try {
            backgroundEditFirstName = attributes.getDrawable(R.styleable.AbstractEditProfileView_backgroundEditUserEdits);
            backgroundEditLastName = attributes.getDrawable(R.styleable.AbstractEditProfileView_backgroundEditUserEdits);
            backgroundButton = attributes.getDrawable(R.styleable.AbstractEditProfileView_backgroundEditUserButton);

            colorTextEdits = attributes.getColorStateList(R.styleable.AbstractEditProfileView_colorEditUserEdits);
            colorHintEdits = attributes.getColorStateList(R.styleable.AbstractEditProfileView_colorEditUserHintEdits);
            colorTextLabels = attributes.getColorStateList(R.styleable.AbstractEditProfileView_colorEditUserLabels);
            colorTextButtons = attributes.getColorStateList(R.styleable.AbstractEditProfileView_colorEditUserTextButton);

            dimenLabelsText = attributes.getDimensionPixelSize(R.styleable.AbstractEditProfileView_dimenEditUserLabels, R.dimen.text_18);
            dimenEditsText = attributes.getDimensionPixelSize(R.styleable.AbstractEditProfileView_dimenEditUserEdits, R.dimen.text_18);
            dimenButtonsText = attributes.getDimensionPixelSize(R.styleable.AbstractEditProfileView_dimenEditUserButton, R.dimen.text_18);
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


        if (backgroundButton != null) {
            buttonSaveChanges.setBackgroundDrawable(backgroundButton);
        }

        if (colorTextEdits != null) {
            editFirstName.setTextColor(colorTextEdits);
            editLastName.setTextColor(colorTextEdits);
        }

        if (colorHintEdits != null) {
            editFirstName.setHintTextColor(colorHintEdits);
            editLastName.setHintTextColor(colorHintEdits);
        }

        if (colorTextLabels != null) {
            labelEmail.setTextColor(colorTextLabels);
        }

        if (colorTextButtons != null) {
            buttonSaveChanges.setTextColor(colorTextButtons);
        }

        editFirstName.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimenEditsText);
        editLastName.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimenEditsText);
        labelEmail.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimenLabelsText);
        buttonSaveChanges.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimenButtonsText);
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
     * Method which provide to getting of the layout ID
     *
     * @return layout ID
     */
    @Override
    protected int getLayoutId() {
        return R.layout.view_edit_profile;
    }

    /**
     * Method which provide the action when view will create
     */
    @Override
    protected void onCreateView() {
        setOnClickListeners(viewEditImage, buttonSaveChanges);
    }

    /**
     * Method which provide the action for onClickListener
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.viewEditImage) {
            onChooseImage();
        } else if (v.getId() == R.id.buttonSaveChanges) {
            presenter.updateUserProfile();
        }
    }

    /**
     * Method which provide the receiving of the activity results
     * (WARNING: Should be always call in the onActivityResult inside the view)
     *
     * @param requestCode request code
     * @param data        data
     */
    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == K_IMAGE_PICK_INTENT_CODE) {
            Uri selectedImage = data.getData();
            final String picturePath = FileHelper.getPath(getContext(), selectedImage);
            if (null != picturePath) {
                Glide.with(getContext()).load(selectedImage).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>(200, 200) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        setImageFromBitmap(bitmap, Attachment.getMimeType(picturePath, Attachment.MIME_TYPE_IMAGE));
                    }
                });
            } else {
                Log.w(TAG, "Failed to load image from Uri " + selectedImage + ", trying to use inputstream");

                Bitmap bitmap = FileHelper.getImageBitmap(getContext(), selectedImage);
                if (null != bitmap) {
                    setImageFromBitmap(bitmap, FileHelper.getMimeType(getContext(), selectedImage));
                }
            }
        }
    }

    /**
     * Method which provide the setting image from bitmap
     *
     * @param bitmap   current bitmap
     * @param mimeType current mime type
     */
    private void setImageFromBitmap(final Bitmap bitmap, final String mimeType) {
        circleImageView.setImageBitmap(bitmap);
        if (bitmap != null) {
            presenter.updateServerAvatar(bitmap, mimeType);
        }
    }

    /**
     * Method which provide the image choosing from the gallery
     */
    @Override
    public void onChooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResults(intent, K_IMAGE_PICK_INTENT_CODE);
    }

    /**
     * Method which provide the setting up of the user information inside the view
     *
     * @param email     user email
     * @param firstName user first name
     * @param lastName  user last name
     */
    @Override
    public void onSetupUserInformation(@NonNull String email, @NonNull String firstName, @NonNull String lastName) {
        labelEmail.setText(email);
        editFirstName.setText(firstName);
        editLastName.setText(lastName);
    }

    /**
     * Method which provide the updating of the user avatar
     */
    @Override
    public void onUpdateUserAvatar(@Nullable User currentUser) {
        if ((currentUser != null) && (currentUser.getAvatarUrl() != null)) {
            String url = currentUser.getAvatarUrl();
            Log.d(TAG, url);
            Glide.with(getContext()).load(url)
                    .placeholder(R.drawable.image_no_avatar)
                    /*Only listeners working for the Galaxy Note 3-5 and S4-S4, this is the Glide issue*/
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            circleImageView.setImageDrawable(resource);
                            return false;
                        }
                    })
                    .centerCrop().into(circleImageView);
        }
    }

    /**
     * Method which provide to getting of the getting user
     *
     * @return current callback
     */
    @Nullable
    @Override
    public EditProfileContract.OnEditUserCallback getCallback() {
        return editUserCallback;
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

        if (firstName == null || firstName.isEmpty() == true) {
            editFirstName.requestFocus();
            editFirstName.setError("First name shouldn't be empty");
            return false;
        }
        if (lastName == null || lastName.isEmpty() == true) {
            editLastName.requestFocus();
            editLastName.setError("First name shouldn't be empty");
            return false;
        }

        return true;
    }

    /**
     * Method which provide to getting of the first name
     *
     * @return first name
     */
    @NonNull
    @Override
    public String getFirstName() {
        return editFirstName.getText().toString().trim();
    }

    /**
     * Method which provide to getting of the last name
     *
     * @return last name
     */
    @NonNull
    @Override
    public String getLastName() {
        return editLastName.getText().toString().trim();
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

    //SETTERS

    /**
     * Method which provide to setting of the edit user callback
     *
     * @param editUserCallback
     */
    public void setEditUserCallback(EditProfileContract.OnEditUserCallback editUserCallback) {
        this.editUserCallback = editUserCallback;
    }
}
