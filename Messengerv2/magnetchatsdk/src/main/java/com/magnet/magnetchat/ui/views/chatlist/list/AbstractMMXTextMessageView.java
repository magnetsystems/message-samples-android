package com.magnet.magnetchat.ui.views.chatlist.list;

import android.content.Context;
import android.util.AttributeSet;

import com.magnet.magnetchat.presenters.chatlist.MMXMessageContract;
import com.magnet.magnetchat.ui.views.abs.ViewProperty;

/**
 * Created by aorehov on 05.05.16.
 */
public abstract class AbstractMMXTextMessageView<T extends ViewProperty> extends BaseMMXMessageView<T, MMXMessageContract.Presenter> implements MMXMessageContract.View {
    public AbstractMMXTextMessageView(Context context) {
        super(context);
    }

    public AbstractMMXTextMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractMMXTextMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
