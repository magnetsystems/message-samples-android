package com.magnet.magnetchat.ui.views.chatlist.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.model.MMXMessageWrapper;
import com.magnet.magnetchat.presenters.chatlist.BaseMMXMessagePresenter;
import com.magnet.magnetchat.ui.views.abs.BaseMMXTypedView;
import com.magnet.magnetchat.ui.views.abs.ViewProperty;
import com.magnet.magnetchat.ui.views.section.chat.CircleNameView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by aorehov on 05.05.16.
 */
public abstract class BaseMMXMessageView<T extends ViewProperty, P extends BaseMMXMessagePresenter> extends BaseMMXTypedView<MMXMessageWrapper, T> {

    CircleNameView uiLettersView;
    CircleImageView uiUserPicView;
    TextView uiDate;
    TextView uiSenderName;

    private P presenter;

    public BaseMMXMessageView(Context context) {
        super(context);
    }

    public BaseMMXMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseMMXMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLinkingViews(View baseView) {
        uiLettersView = findView(baseView, R.id.mmx_msg_pic_letters);
        uiUserPicView = findView(baseView, R.id.mmx_msg_pic_origin);
        uiDate = findView(baseView, R.id.mmx_msg_date);
        uiSenderName = findView(baseView, R.id.mmx_sender);
    }

    @Override
    protected void onCreateView() {
        presenter = readPresenter();
    }

    public P getPresenter() {
        return presenter;
    }

    @Override
    public void setObject(MMXMessageWrapper object) {
        super.setObject(object);
        presenter.setMMXMessage(object);
    }

    protected abstract P readPresenter();
}
