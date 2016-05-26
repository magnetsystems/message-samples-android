package com.magnet.magnetchat.ui.views.chatlist.poll;

import android.content.Context;
import android.util.AttributeSet;

import com.magnet.magnetchat.R;

/**
 * Created by aorehov on 10.05.16.
 */
public class DefaultMMXPollItemAnotherView extends DefaultMMXPollItemView {
    public DefaultMMXPollItemAnotherView(Context context) {
        super(context);
    }

    public DefaultMMXPollItemAnotherView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultMMXPollItemAnotherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_mmxchat_msg_poll_item_another;
    }

    @Override
    protected void onCreateView() {

    }
}
