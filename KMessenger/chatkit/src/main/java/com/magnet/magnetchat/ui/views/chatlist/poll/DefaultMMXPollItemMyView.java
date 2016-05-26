package com.magnet.magnetchat.ui.views.chatlist.poll;

import android.content.Context;
import android.util.AttributeSet;

import com.magnet.magnetchat.R;

/**
 * Created by aorehov on 10.05.16.
 */
public class DefaultMMXPollItemMyView extends DefaultMMXPollItemView {
    public DefaultMMXPollItemMyView(Context context) {
        super(context);
    }

    public DefaultMMXPollItemMyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultMMXPollItemMyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_mmxchat_msg_poll_item_my;
    }

    @Override
    protected void onCreateView() {

    }
}
