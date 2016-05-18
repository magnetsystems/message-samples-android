package com.magnet.magnetchat.ui.views.chatlist.list;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.presenters.chatlist.MMXMessageContract;

import java.util.Date;

/**
 * Created by aorehov on 05.05.16.
 */
public abstract class DefaultMMXMessageView extends AbstractMMXTextMessageView<MMXTextProperty> {

    private TextView uiMessageText;

    public DefaultMMXMessageView(Context context) {
        super(context);
    }

    public DefaultMMXMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultMMXMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLinkingViews(View baseView) {
        super.onLinkingViews(baseView);
        uiMessageText = findView(baseView, R.id.mmx_msg_text);
    }

    @Override
    public void onShowTextMessage(String message) {
        uiMessageText.setText(message);
    }

    @Override
    public void isNeedShowDate(boolean isShowDate) {
        uiDate.setVisibility(isShowDate ? VISIBLE : GONE);
    }

    @Override
    public void onSetPostDate(Date date) {
        uiDate.setText(date.toString());
    }

    @Override
    public void onShowUserLetters(String letters) {
        uiLettersView.setUserName(letters);
        uiLettersView.setVisibility(VISIBLE);
        uiUserPicView.setVisibility(GONE);
    }

    @Override
    public void onShowUserPicture(String url) {
        uiLettersView.setVisibility(GONE);
        uiUserPicView.setVisibility(VISIBLE);
        Glide.with(getContext())
                .load(Uri.parse(url))
                .error(R.drawable.add_user_icon)
                .into(uiUserPicView);


    }

    @Override
    public void onSenderName(String name) {
        if (uiSenderName != null) uiSenderName.setText(name);
    }

    @Override
    protected MMXMessageContract.Presenter readPresenter() {
        return ChatSDK.getMMXMessagPresenterFactory().createMessagePresenter(this);
    }

    @Override
    public void setProperties(MMXTextProperty property) {

    }
}
