package com.magnet.magnetchat.ui.views.users;

import android.content.Context;
import android.util.AttributeSet;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.presenters.UserListContract;


/**
 * Created by aorehov on 12.05.16.
 */
public class DefaultMMXAllUserListView extends DefaultMMXUserListView {

    public DefaultMMXAllUserListView(Context context) {
        super(context);
    }

    public DefaultMMXAllUserListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultMMXAllUserListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected UserListContract.Presenter createUserListPresenter(String name) {
        return ChatSDK.getPresenterFactory().createAllUserListPresenter(this);
    }
}
