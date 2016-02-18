package com.magnet.magnetchat.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.ui.views.abs.BaseView;

import butterknife.InjectView;

public class AskMagnetView extends BaseView {

    @InjectView(R.id.ivSecondaryNewMsg)
    ImageView ivUnreadMessage;

    public AskMagnetView(Context context) {
        super(context);
    }

    public AskMagnetView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AskMagnetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_ask_magnet;
    }

    @Override
    protected void onCreateView() {

    }

    public void setUnreadMessage(boolean hasUnreadMessage) {
        if (hasUnreadMessage) {
            ivUnreadMessage.setVisibility(VISIBLE);
        } else {
            ivUnreadMessage.setVisibility(INVISIBLE);
        }
    }

}
