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
import com.magnet.magnetchat.ui.factories.MMXListItemFactory;
import com.magnet.magnetchat.ui.views.chatlist.poll.AbstractMMXPollItemView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aorehov on 06.05.16.
 */
public abstract class DefaultMMXPollMessageView extends AbstractMMXPollMessageView<MMXPollProperty> implements AbstractMMXPollItemView.OnPollItemClickListener {

    TextView uiPollType;
    TextView uiPollQuestion;
    LinearLayout uiPollQuestions;
    View uiSubmit;
    Map<String, AbstractMMXPollItemView> pollViews = new HashMap<>();
    //    ArrayList<String> ids = new ArrayList<>();
    private MMXListItemFactory factory;

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

        factory = ChatSDK.getMmxListItemFactory();

        uiPollType = findView(baseView, R.id.mmx_poll_type);
        uiPollQuestion = findView(baseView, R.id.mmx_poll_question);
        uiPollQuestions = findView(baseView, R.id.mmx_poll_answers);

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
        uiPollQuestions.removeAllViews();
        for (int index = 0; index < data.size(); index++) {
            MMXPollOptionWrapper option = data.get(index);
            updateItem(index, option);
        }
    }

    private void updateItem(int index, MMXPollOptionWrapper option) {
        if (index > 0) {
            View childAt = uiPollQuestions.getChildAt(index);
            if (childAt != null) uiPollQuestions.removeView(childAt);
        }

        AbstractMMXPollItemView view = getView(option);
//        ids.add(index, option.getId());
        uiPollQuestions.addView(view, index);
        view.setObject(option);
    }

    private AbstractMMXPollItemView getView(MMXPollOptionWrapper opt) {
        String id = opt.getId();
        AbstractMMXPollItemView view = pollViews.get(id);
        if (view == null) {
            view = (AbstractMMXPollItemView) factory.createView(getContext(), opt.getType());
            view.setListener(this);
        }

        return view;
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

    @Override
    public void onClicked(MMXPollOptionWrapper wrapper) {
        getPresenter().onNeedChangedState(wrapper);
    }
}
