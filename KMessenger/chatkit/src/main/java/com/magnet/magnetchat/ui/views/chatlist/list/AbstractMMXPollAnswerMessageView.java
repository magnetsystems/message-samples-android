package com.magnet.magnetchat.ui.views.chatlist.list;

import android.content.Context;
import android.util.AttributeSet;

import com.magnet.magnetchat.presenters.chatlist.MMXMessageContract;
import com.magnet.magnetchat.ui.views.abs.ViewProperty;

/**
 * Created by aorehov on 10.05.16.
 */
public abstract class AbstractMMXPollAnswerMessageView<T extends MMXMessageBaseProperty> extends BaseMMXMessageView<T, MMXMessageContract.Presenter> {
    public AbstractMMXPollAnswerMessageView(Context context) {
        super(context);
    }

    public AbstractMMXPollAnswerMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractMMXPollAnswerMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
