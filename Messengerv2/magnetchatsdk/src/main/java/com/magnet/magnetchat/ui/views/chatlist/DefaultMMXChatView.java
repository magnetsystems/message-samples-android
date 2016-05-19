package com.magnet.magnetchat.ui.views.chatlist;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.ui.views.abs.ViewProperty;

/**
 * Use next parameters for customization
 * <p/>
 * app:chatlist_background="@drawable/splash"
 * app:post_background="@color/colorAccentCover"
 * app:post_height="?attr/actionBarSize"
 * app:postdivider_color="@color/common_google_signin_btn_text_dark_focused"
 * app:postdivider_height="@dimen/dimen_3"
 * <p/>
 * Created by aorehov on 04.05.16.
 */
public class DefaultMMXChatView extends MMXChatView<DefaultMMXChatView.MMXChatProperty> {
    private FrameLayout uiListContainer;
    private FrameLayout uiPostContainer;
    private View uiDivider;

    public DefaultMMXChatView(Context context) {
        super(context);
    }

    public DefaultMMXChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultMMXChatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected MMXChatProperty onReadAttributes(AttributeSet attrs) {
        TypedArray arr = readTypedArray(attrs, R.styleable.DefaultMMXChatView);
        try {
            MMXChatProperty prop = new MMXChatProperty();
            prop.chatlist_background = arr.getDrawable(R.styleable.DefaultMMXChatView_chatlist_background);
            prop.postdivider_color = arr.getColor(R.styleable.DefaultMMXChatView_postdivider_color, -1);
            prop.postdivider_height = arr.getDimensionPixelSize(R.styleable.DefaultMMXChatView_postdivider_height, -1);
            prop.post_background = arr.getDrawable(R.styleable.DefaultMMXChatView_post_background);
            prop.post_height = arr.getDimensionPixelSize(R.styleable.DefaultMMXChatView_post_height, -1);
            return prop;
        } finally {
            arr.recycle();
        }
    }

    @Override
    protected void onApplyAttributes(MMXChatProperty prop) {
        if (prop.chatlist_background != null)
            uiListContainer.setBackground(prop.chatlist_background);
        if (prop.postdivider_color != -1) uiDivider.setBackgroundColor(prop.postdivider_color);
        if (prop.postdivider_height != -1)
            uiDivider.getLayoutParams().height = prop.postdivider_height;
        if (prop.post_background != null) uiPostContainer.setBackground(prop.post_background);
        if (prop.post_height != -1)
            uiPostContainer.getLayoutParams().height = prop.post_height;

    }

    @Override
    protected void onAttachViewToParent(MMXChatListView mmxChatListView, MMXPostMessageView mmxPostMessageView) {
        if (uiListContainer.getChildCount() != 0) uiListContainer.removeAllViews();
        uiListContainer.addView(mmxChatListView);

        if (uiPostContainer.getChildCount() != 0) uiPostContainer.removeAllViews();
        uiPostContainer.addView(mmxPostMessageView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_mmxchat;
    }

    @Override
    protected void onLinkingViews(View baseView) {
        uiListContainer = findView(baseView, R.id.mmx_chat_list);
        uiPostContainer = findView(baseView, R.id.mmx_chat_post);
        uiDivider = findView(baseView, R.id.mmx_divider);
    }

    public class MMXChatProperty extends ViewProperty {
        Drawable chatlist_background;
        int postdivider_height;
        int postdivider_color;
        Drawable post_background;
        int post_height;
    }
}
