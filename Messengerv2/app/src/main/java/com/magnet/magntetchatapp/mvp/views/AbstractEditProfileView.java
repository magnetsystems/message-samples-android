package com.magnet.magntetchatapp.mvp.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.magnet.magnetchat.helpers.FileHelper;
import com.magnet.magntetchatapp.R;
import com.magnet.magntetchatapp.mvp.abs.BasePresenterView;
import com.magnet.magntetchatapp.mvp.api.EditProfileContract;
import com.magnet.magntetchatapp.mvp.presenters.DefaultEditProfilePresenter;
import com.magnet.max.android.Attachment;
import com.magnet.max.android.User;

import butterknife.InjectView;

/**
 * Created by dlernatovich on 3/16/16.
 */
public class AbstractEditProfileView extends BasePresenterView<EditProfileContract.Presenter> implements EditProfileContract.View {

    public static final int K_IMAGE_PICK_INTENT_CODE = 0x64;
    private static final String TAG = "AbstractEditProfileView";
    private EditProfileContract.OnEditUserCallback editUserCallback;

    @InjectView(R.id.viewEditImage)
    View viewEditImage;
    @InjectView(R.id.imageUser)
    ImageView circleImageView;
    @InjectView(R.id.labelEmail)
    TextView labelEmail;
    @InjectView(R.id.editFirstName)
    AppCompatEditText editFirstName;
    @InjectView(R.id.editLastName)
    AppCompatEditText editLastName;


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
     * Method which provide the getting of the current presenter
     *
     * @return current view presenter
     */
    @NonNull
    @Override
    public EditProfileContract.Presenter getPresenter() {
        return new DefaultEditProfilePresenter(this);
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
        setOnClickListeners(viewEditImage);
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
            Glide.with(getContext())
                    .load(User.getCurrentUser().getAvatarUrl())
                    .placeholder(R.drawable.image_no_avatar)
                    .centerCrop()
                    .into(circleImageView);
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
