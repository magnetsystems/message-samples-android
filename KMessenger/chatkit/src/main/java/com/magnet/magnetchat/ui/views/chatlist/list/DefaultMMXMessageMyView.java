package com.magnet.magnetchat.ui.views.chatlist.list;

import android.content.Context;
import android.util.AttributeSet;

import com.magnet.magnetchat.R;

/**
 * Created by aorehov on 05.05.16.
 */
public class DefaultMMXMessageMyView extends DefaultMMXMessageView {
    public DefaultMMXMessageMyView(Context context) {
        super(context);
    }

    public DefaultMMXMessageMyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultMMXMessageMyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_mmxchat_msg_my;
    }
}
