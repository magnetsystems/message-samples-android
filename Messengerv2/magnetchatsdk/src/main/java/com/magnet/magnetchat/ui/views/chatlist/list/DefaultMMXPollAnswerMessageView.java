package com.magnet.magnetchat.ui.views.chatlist.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.presenters.chatlist.MMXMessageContract;

import java.util.Date;

/**
 * Created by aorehov on 10.05.16.
 */
public class DefaultMMXPollAnswerMessageView extends AbstractMMXPollAnswerMessageView<MMXPollAnswerProperty> implements MMXMessageContract.View {

    TextView uiText;

    public DefaultMMXPollAnswerMessageView(Context context) {
        super(context);
    }

    public DefaultMMXPollAnswerMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultMMXPollAnswerMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLinkingViews(View baseView) {
        super.onLinkingViews(baseView);
        uiText = findView(baseView, R.id.mmx_msg_text);
    }

    @Override
    protected MMXMessageContract.Presenter readPresenter() {
        return ChatSDK.getMMXMessagPresenterFactory().createMessagePresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_mmxchat_msg_poll_answers;
    }

    @Override
    public void setProperties(MMXPollAnswerProperty property) {

    }

    @Override
    public void onShowTextMessage(String message) {
        uiText.setText(message);
    }

    @Override
    public void isNeedShowDate(boolean isShowDate) {
        if (uiDate != null) uiDate.setVisibility(isShowDate ? VISIBLE : GONE);
    }

    @Override
    public void onSetPostDate(Date date) {
        if (uiDate != null) uiDate.setText(date.toString());
    }

    @Override
    public void onShowUserLetters(String letters) {
//STUB
    }

    @Override
    public void onShowUserPicture(String url) {
//STUB
    }

    @Override
    public void onSenderName(String name) {
//STUB
    }
}
