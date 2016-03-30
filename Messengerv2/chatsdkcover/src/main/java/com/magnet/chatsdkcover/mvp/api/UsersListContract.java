package com.magnet.chatsdkcover.mvp.api;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.magnet.chatsdkcover.R;
import com.magnet.chatsdkcover.mvp.abs.BaseContract;
import com.magnet.chatsdkcover.ui.custom.AdapteredRecyclerView;
import com.magnet.chatsdkcover.ui.custom.CircleNameView;
import com.magnet.max.android.User;

import java.lang.ref.WeakReference;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Artli_000 on 29.03.2016.
 */
public interface UsersListContract {

    //Querry params
    String QUERY_TEMPLATE = "lastName:%s*";
    String QUERY_SEARCH_ALL_USERS = "*";
    String SORT_ORDER = "lastName:asc";
    //Offset params
    int LIMIT = 15;


    /**
     * View
     */
    interface View extends BaseContract.BaseView {

        /**
         * Method which privide the users adding to the list
         *
         * @param userObjects users list
         */
        void addUsers(@NonNull final List<UserObject> userObjects);

        /**
         * Method which provide the setting of the users list
         *
         * @param userObjects users list
         */
        void setUsers(@NonNull final List<UserObject> userObjects);

        /**
         * Method which provide the setting of the OnLazyLoadCallback
         *
         * @param lazyLoadCallback current lazyLoadCallback
         */
        void setLazyLoadCallback(@NonNull final AdapteredRecyclerView.OnLazyLoadCallback lazyLoadCallback);

        /**
         * Method which provide to getting of the selected user objects list
         *
         * @return user objects list
         */
        List<UserObject> getSelectedUserObjects();

        /**
         * Method which provide to getting of the selecting user objects
         *
         * @return user objects
         */
        List<User> getSelectedUsers();

        /**
         * Method which provide the switch loading message
         *
         * @param message    message
         * @param isNeedShow is need show loading message
         */
        void switchLoading(@Nullable final String message, final boolean isNeedShow);

    }

    /**
     * Presenter
     */
    interface Presenter extends BaseContract.BasePresenter, AdapteredRecyclerView.OnLazyLoadCallback {

        /**
         * Method which provide to getting of the all users with offset
         *
         * @param offset current offset
         */
        void getAllUsers(final int offset);

        /**
         * Method which provide the users getting
         *
         * @param filter filter by last name
         * @param offset offset
         */
        void getUsers(@NonNull final String filter, final int offset);

        /**
         * Method which provide the users getting
         *
         * @param filter    filter by last name
         * @param offset    offset
         * @param sortOrder sort order
         */
        void getUsers(@NonNull final String filter, final int offset, @NonNull final String sortOrder);

        /**
         * Method which provide the adding of the users list
         *
         * @param userObjects users list
         */
        void addUsers(@NonNull final List<UserObject> userObjects);

        /**
         * Method which provide the setting of the users
         *
         * @param userObjects current users
         */
        void setUsers(@NonNull final List<UserObject> userObjects);

        /**
         * Method which provide the switch loading message
         *
         * @param message    message
         * @param isNeedShow is need show loading message
         */
        void switchLoading(@Nullable final String message, final boolean isNeedShow);
    }


    //=======================================================================================
    //==================================RECYCLER ITEMS=======================================
    //=======================================================================================


    /**
     * User view
     */
    class UserRecyclerView extends AdapteredRecyclerView.BaseRecyclerItem<UserObject> {

        private ViewGroup viewContent;
        private CircleNameView viewCircleName;
        private CircleImageView imageAvatar;
        private AppCompatTextView labelFirstName;
        private AppCompatTextView labelLastName;

        /**
         * Constructor
         *
         * @param context context
         */
        public UserRecyclerView(Context context) {
            super(context);
        }

        /**
         * Method which provide the setting up for the current recycler item
         *
         * @param baseObject current object
         */
        @Override
        public void setUp(@NonNull UserObject baseObject) {
            viewCircleName.setUserName(baseObject.fullName);
            labelFirstName.setText(baseObject.firstName);
            labelLastName.setText(baseObject.lastName);

            if (baseObject.avatarUrl != null) {
                Glide.with(getContext())
                        .load(baseObject.avatarUrl)
                        .centerCrop()
                        .fitCenter()
                        .listener(glideCallback)
                        .into(imageAvatar);
            }

        }

        /**
         * Method which provide to getting of the layout ID
         *
         * @return layout ID
         */
        @Override
        protected int getLayoutId() {
            return R.layout.item_view_user_cover;
        }

        /**
         * Method which provide the interface linking
         */
        @Override
        protected void onLinkInterface() {
            viewContent = (ViewGroup) findViewById(R.id.viewContent);
            viewCircleName = (CircleNameView) findViewById(R.id.viewCircleName);
            imageAvatar = (CircleImageView) findViewById(R.id.imageAvatar);
            labelFirstName = (AppCompatTextView) findViewById(R.id.labelFirstName);
            labelLastName = (AppCompatTextView) findViewById(R.id.labelLastName);
        }

        /**
         * Method which provide the getting of the clicked view ID
         *
         * @return clicked view ID
         */
        @Override
        protected int getClickedID() {
            return R.id.viewContent;
        }

        /**
         * Method which provide the action when view will create
         */
        @Override
        protected void onCreateView() {
            viewContent.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    switchBackgroundColor();
                }
            });
        }

        /**
         * Method which provide the performing of the switcing of the background color with accordance to state
         */
        private void switchBackgroundColor() {
            if (objectReference != null
                    && objectReference.get() != null
                    && (objectReference.get() instanceof UserObject) == true
                    && getContext() != null
                    && getContext().getResources() != null) {
                UserObject baseObject = objectReference.get();
                baseObject.isSelected = !baseObject.isSelected;
                if (baseObject.isSelected == true) {
                    switchBackgroundColor(getContext().getResources().getColor(android.R.color.darker_gray));
                } else {
                    switchBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
                }
            }
        }

        /**
         * Method which provide the color setting for the background
         *
         * @param colorStateList color state list
         */
        private void switchBackgroundColor(ColorStateList colorStateList) {
            switchBackgroundColor(colorStateList.getColorForState(EMPTY_STATE_SET, android.R.color.transparent));
        }

        /**
         * Method which provide the setting of the background color
         *
         * @param color color
         */
        private void switchBackgroundColor(@ColorInt int color) {
            viewContent.setBackgroundColor(color);
        }

        /**
         * Callback which provide to loading of the image into the channel image view
         */
        private final RequestListener<String, GlideDrawable> glideCallback = new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                if (e != null) {
                    Log.e("ChannelRecyclerItem", e.toString());
                }
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                if (imageAvatar != null && resource != null) {
                    imageAvatar.setImageDrawable(resource);
                }
                return false;
            }
        };
    }

    /**
     * User object
     */

    class UserObject extends AdapteredRecyclerView.BaseObject {

        private final User user;
        private boolean isSelected;
        private WeakReference<UserRecyclerView> itemWeakReference;

        private final String firstName;
        private final String lastName;
        private final String avatarUrl;
        private final String fullName;

        /**
         * Constructor
         *
         * @param user user object
         */
        public UserObject(@NonNull User user) {
            this.user = user;
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.avatarUrl = user.getAvatarUrl();
            this.fullName = String.format("%s %s", this.firstName, this.lastName);
        }

        /**
         * Method which provide the getting of the current recycler item
         *
         * @param context current context
         * @return current instance for the Recycler item
         */
        @Override
        public AdapteredRecyclerView.BaseRecyclerItem getRecyclerItem(@NonNull Context context) {
            UserRecyclerView baseRecyclerItem;
            if (itemWeakReference == null
                    || itemWeakReference.get() == null) {
                baseRecyclerItem = new UserRecyclerView(context);
                itemWeakReference = new WeakReference<UserRecyclerView>(baseRecyclerItem);
            }
            return new UserRecyclerView(context);
        }

        /**
         * Method which provide the getting user
         *
         * @return current user
         */
        @NonNull
        public User getUser() {
            return user;
        }

        /**
         * Method which provide the checking if current user object is selected
         *
         * @return checking results
         */
        public boolean isSelected() {
            return isSelected;
        }

        /**
         * Method which provide the setting of the selected property
         *
         * @param isSelected boolean value
         */
        public void setIsSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }
    }

}
