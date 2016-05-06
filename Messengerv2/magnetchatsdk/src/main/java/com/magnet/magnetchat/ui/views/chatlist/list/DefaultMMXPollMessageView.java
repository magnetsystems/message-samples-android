package com.magnet.magnetchat.ui.views.chatlist.list;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.model.MMXPollOptionWrapper;
import com.magnet.magnetchat.presenters.chatlist.MMXPollContract;

import java.util.Date;
import java.util.List;

/**
 * Created by aorehov on 06.05.16.
 */
public abstract class DefaultMMXPollMessageView extends AbstractMMXPollMessageView<MMXPollProperty> {

    TextView uiPollType;
    TextView uiPollQuestion;
    LinearLayout uiPollQuestions;
    View uiSubmit;

    public DefaultMMXPollMessageView(Context context) {
        super(context);
    }

    public DefaultMMXPollMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultMMXPollMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLinkingViews(View baseView) {
        super.onLinkingViews(baseView);
        uiPollType = findView(R.id.mmx_poll_type);
        uiPollQuestion = findView(R.id.mmx_poll_question);
        uiSubmit = findView(R.id.mmx_submit);
        uiSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mmx_submit) {

        } else
            super.onClick(v);
    }

    @Override
    public void onPollType(int resId) {
        uiPollType.setText(resId);
    }

    @Override
    public void onPollQuestion(String question) {
        uiPollQuestion.setText(question);
    }

    @Override
    public void onPollAnswersReceived(List<MMXPollOptionWrapper> data) {
// TODO implement ui logic for POLL items
    }

    @Override
    public void onPollAnswersUpdate(MMXPollOptionWrapper option) {
// TODO implement ui logic for POLL items
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
                .placeholder(R.drawable.add_user_icon)
                .into(uiUserPicView);
    }

    @Override
    public void onSenderName(String name) {
        if (uiSenderName != null) uiSenderName.setText(name);
    }

    @Override
    protected MMXPollContract.Presenter readPresenter() {
        return ChatSDK.getMMXMessagPresenterFactory().createPollPresenter(this);
    }

    @Override
    public void setProperties(MMXPollProperty property) {

    }
}
