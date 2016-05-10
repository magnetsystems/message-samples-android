package com.magnet.magnetchat.ui.views.chatlist.poll;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.magnet.magnetchat.R;

/**
 * Created by aorehov on 10.05.16.
 */
public abstract class DefaultMMXPollItemView extends AbstractMMXPollItemView<PollItemProperty> {

    private TextView uiText;
    private TextView uiCounter;
    private View uiContainer;

    public DefaultMMXPollItemView(Context context) {
        super(context);
    }

    public DefaultMMXPollItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultMMXPollItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLinkingViews(View baseView) {
        uiText = findView(baseView, R.id.mmx_poll_answer_text);
        uiCounter = findView(baseView, R.id.mmx_poll_answer_counter);
        uiContainer = findView(baseView, R.id.mmx_poll_answer_container);
    }

    @Override
    protected void updateCount(long votesCount) {
        uiCounter.setText(String.valueOf(votesCount));
    }

    @Override
    protected void updateText(String voteText) {
        uiText.setText(voteText);
    }

    @Override
    protected void updateIsSelected(boolean selected) {
        int drawable = selected ? R.drawable.shape_mmx_poll_my_item_selected : R.drawable.selector_mmx_poll_my_item;
        uiContainer.setBackgroundResource(drawable);
    }

    @Override
    public void setProperties(PollItemProperty property) {

    }

    @Override
    protected View getClickableView() {
        return uiContainer;
    }


}
