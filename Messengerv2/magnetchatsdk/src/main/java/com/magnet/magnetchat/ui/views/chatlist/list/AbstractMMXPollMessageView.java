package com.magnet.magnetchat.ui.views.chatlist.list;

import android.content.Context;
import android.util.AttributeSet;

import com.magnet.magnetchat.presenters.chatlist.MMXPollContract;
import com.magnet.magnetchat.ui.views.abs.ViewProperty;

import java.util.Date;

/**
 * Created by aorehov on 05.05.16.
 */
public abstract class AbstractMMXPollMessageView<T extends ViewProperty> extends BaseMMXMessageView<T, MMXPollContract.Presenter> implements MMXPollContract.View {
    public AbstractMMXPollMessageView(Context context) {
        super(context);
    }

    public AbstractMMXPollMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractMMXPollMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public void onSetPostDate(Date date) {
        setDate(date);
    }

}
