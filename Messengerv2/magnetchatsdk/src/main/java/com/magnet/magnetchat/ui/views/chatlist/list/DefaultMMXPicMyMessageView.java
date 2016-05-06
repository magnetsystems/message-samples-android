package com.magnet.magnetchat.ui.views.chatlist.list;

import android.content.Context;
import android.util.AttributeSet;

import com.magnet.magnetchat.R;

/**
 * Created by aorehov on 06.05.16.
 */
public class DefaultMMXPicMyMessageView extends DefaultMMXPictureMessageView {
    public DefaultMMXPicMyMessageView(Context context) {
        super(context);
    }

    public DefaultMMXPicMyMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultMMXPicMyMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_mmxchat_msg_pic_my;
    }
}
