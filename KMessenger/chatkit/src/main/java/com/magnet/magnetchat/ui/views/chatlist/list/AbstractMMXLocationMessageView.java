package com.magnet.magnetchat.ui.views.chatlist.list;

import android.content.Context;
import android.util.AttributeSet;

import com.magnet.magnetchat.presenters.chatlist.MMXLocationContract;
import com.magnet.magnetchat.ui.views.abs.ViewProperty;

import java.util.Date;

/**
 * Created by aorehov on 05.05.16.
 */
public abstract class AbstractMMXLocationMessageView<T extends MMXMessageBaseProperty>
        extends BaseMMXMessageView<T, MMXLocationContract.Presenter> implements MMXLocationContract.View {
    public AbstractMMXLocationMessageView(Context context) {
        super(context);
    }

    public AbstractMMXLocationMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractMMXLocationMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onSetPostDate(Date date) {
        setDate(date);
    }

}
