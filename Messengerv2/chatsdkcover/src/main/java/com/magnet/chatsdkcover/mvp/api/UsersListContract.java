package com.magnet.chatsdkcover.mvp.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.magnet.chatsdkcover.R;
import com.magnet.chatsdkcover.mvp.abs.BaseContract;
import com.magnet.chatsdkcover.ui.custom.AdapteredRecyclerView;
import com.magnet.chatsdkcover.ui.custom.CircleNameView;

import java.lang.ref.WeakReference;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Artli_000 on 29.03.2016.
 */
public interface UsersListContract {

    interface View extends BaseContract.BaseView {

    }

    interface Presenter extends BaseContract.BasePresenter {

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

        }
    }

    /**
     * User object
     */

    class UserObject extends AdapteredRecyclerView.BaseObject {

        private WeakReference<UserRecyclerView> itemWeakReference;

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
            return itemWeakReference.get();
        }
    }

}
