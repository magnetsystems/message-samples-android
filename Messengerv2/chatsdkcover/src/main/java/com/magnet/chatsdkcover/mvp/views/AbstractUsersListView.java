package com.magnet.chatsdkcover.mvp.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

import com.magnet.chatsdkcover.R;
import com.magnet.chatsdkcover.mvp.abs.BasePresenterView;
import com.magnet.chatsdkcover.mvp.api.UsersListContract;
import com.magnet.chatsdkcover.mvp.presenters.DefaultUsersListPresenter;
import com.magnet.chatsdkcover.ui.custom.AdapteredRecyclerView;
import com.magnet.max.android.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artli_000 on 29.03.2016.
 */
public class AbstractUsersListView extends BasePresenterView<UsersListContract.Presenter> implements UsersListContract.View {

    private AdapteredRecyclerView recyclerView;

    public AbstractUsersListView(Context context) {
        super(context);
    }

    public AbstractUsersListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractUsersListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Method which provide the getting of the current presenter
     *
     * @return current view presenter
     */
    @NonNull
    @Override
    public UsersListContract.Presenter getPresenter() {
        return new DefaultUsersListPresenter(this);
    }

    /**
     * Method which provide to getting of the layout ID
     *
     * @return layout ID
     */
    @Override
    protected int getLayoutId() {
        return R.layout.view_users_list_cover;
    }

    /**
     * Method which provide the interface linking
     */
    @Override
    protected void onLinkInterface() {
        recyclerView = (AdapteredRecyclerView) findViewById(R.id.recyclerView);
    }

    /**
     * Method which provide the action when view will create
     */
    @Override
    protected void onCreateView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getCurrentContext(), 1));
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
     * Method which privide the users adding to the list
     *
     * @param userObjects users list
     */
    @Override
    public void addUsers(@NonNull final List<UsersListContract.UserObject> userObjects) {
        runOnMainThread(0, new OnActionPerformer() {
            @Override
            public void onActionPerform() {
                if (recyclerView != null) {
                    recyclerView.addList(userObjects);
                }
            }
        });
    }

    /**
     * Method which provide the setting of the users list
     *
     * @param userObjects users list
     */
    @Override
    public void setUsers(@NonNull final List<UsersListContract.UserObject> userObjects) {
        runOnMainThread(0, new OnActionPerformer() {
            @Override
            public void onActionPerform() {
                if (recyclerView != null) {
                    recyclerView.setList(userObjects);
                }
            }
        });
    }

    /**
     * Method which provide the setting of the OnLazyLoadCallback
     *
     * @param lazyLoadCallback current lazyLoadCallback
     */
    @Override
    public void setLazyLoadCallback(@NonNull AdapteredRecyclerView.OnLazyLoadCallback lazyLoadCallback) {
        if (recyclerView != null) {
            recyclerView.setLazyLoadCallback(lazyLoadCallback);
        }
    }

    /**
     * Method which provide to getting of the selected user objects list
     *
     * @return user objects list
     */
    @Override
    public List<UsersListContract.UserObject> getSelectedUserObjects() {
        List<UsersListContract.UserObject> userObjects = new ArrayList<>();
        final List<AdapteredRecyclerView.BaseObject> baseObjects = recyclerView.getListItems();
        for (AdapteredRecyclerView.BaseObject baseObject : baseObjects) {
            if (baseObject instanceof UsersListContract.UserObject) {
                UsersListContract.UserObject userObject = (UsersListContract.UserObject) baseObject;
                if (userObject.isSelected() == true) {
                    userObjects.add(userObject);
                }
            }
        }
        return userObjects;
    }

    /**
     * Method which provide to getting of the selecting user objects
     *
     * @return user objects
     */
    @Override
    public List<User> getSelectedUsers() {
        List<User> userObjects = new ArrayList<>();
        final List<AdapteredRecyclerView.BaseObject> baseObjects = recyclerView.getListItems();
        for (AdapteredRecyclerView.BaseObject baseObject : baseObjects) {
            if (baseObject instanceof UsersListContract.UserObject) {
                UsersListContract.UserObject userObject = (UsersListContract.UserObject) baseObject;
                if (userObject.isSelected() == true) {
                    userObjects.add(userObject.getUser());
                }
            }
        }
        return userObjects;
    }
}
