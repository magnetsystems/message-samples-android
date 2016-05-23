package com.magnet.magnetchat.ui.views.chatlist.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.presenters.chatlist.MMXMessageContract;
import com.magnet.magnetchat.presenters.chatlist.MMXMessagePresenterFactory;

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
    protected MMXMessageContract.Presenter readPresenter(MMXMessagePresenterFactory factory) {
        return factory.createMessagePresenter(this);
    }

    @Override
    protected MMXPollAnswerProperty onReadAttributes(AttributeSet attrs) {
        TypedArray arr = readTypedArray(attrs, R.styleable.DefaultMMXPollAnswerMessageView);
        try {
            MMXPollAnswerProperty props = new MMXPollAnswerProperty();
            props.date_textColor = arr.getColor(R.styleable.DefaultMMXPollAnswerMessageView_date_textColor, -1);
            props.date_textSize = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollAnswerMessageView_date_textSize, -1);
            props.common_background = arr.getDrawable(R.styleable.DefaultMMXPollAnswerMessageView_common_background);
            props.text_color = arr.getColor(R.styleable.DefaultMMXPollAnswerMessageView_text_color, -1);
            props.text_size = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollAnswerMessageView_text_size, -1);
            return props;
        } finally {
            arr.recycle();
        }
    }

    @Override
    protected void onApplyAttributes(MMXPollAnswerProperty prop) {
        if (prop.date_textColor != -1) uiDate.setTextColor(prop.date_textColor);
        if (prop.date_textSize != -1)
            uiDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, prop.date_textSize);
        if (prop.common_background != null) setBackground(prop.common_background);
        if (prop.text_color != -1) uiText.setTextColor(prop.text_color);
        if (prop.text_size != -1) uiText.setTextSize(TypedValue.COMPLEX_UNIT_PX, prop.text_size);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_mmxchat_msg_poll_answers;
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
        if (uiDate != null) setDate(date);
    }

    @Override
    public void onShowUserPicture(String url, String name) {
//        STUB
    }

    @Override
    public void onSenderName(String name) {
//STUB
    }
}
