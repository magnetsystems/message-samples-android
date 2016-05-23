package com.magnet.magnetchat.ui.views.chatlist.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.presenters.chatlist.MMXMessageContract;
import com.magnet.magnetchat.presenters.chatlist.MMXMessagePresenterFactory;


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
    protected void onApplyAttributes(MMXTextProperty prop) {
        uiUserLetters.setTextSize(TypedValue.COMPLEX_UNIT_PX, prop.letters_textSize);
        uiUserLetters.setTextColor(prop.letters_textColor);

        FrameLayout.LayoutParams params = (LayoutParams) uiUserPicView.getLayoutParams();
        if (prop.upic_height != -1) params.height = prop.upic_height;
        if (prop.upic_width != -1) params.width = prop.upic_width;
        if (prop.upic_src != null) setDefColorUserPic(prop.upic_src);
        params.setMargins(
                prop.upic_marginLeft,
                prop.upic_marginTop,
                prop.upic_marginRight,
                prop.upic_marginBottom);
        if (prop.upic_borderSize != -1) {
            uiUserPicView.setBorderWidth(prop.upic_borderSize);
            if (prop.upic_borderColor != -1) uiUserPicView.setBorderColor(prop.upic_borderColor);
        }

        if (prop.date_textColor != -1) uiDate.setTextColor(prop.date_textColor);
        if (prop.date_textSize != -1)
            uiDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, prop.date_textSize);

        if (uiSenderName != null) {
            if (prop.uname_textSize != -1)
                uiSenderName.setTextSize(TypedValue.COMPLEX_UNIT_PX, prop.uname_textSize);
            if (prop.uname_textColor != -1) uiSenderName.setTextColor(prop.uname_textColor);
            LinearLayout.LayoutParams sparams = (LinearLayout.LayoutParams) uiSenderName.getLayoutParams();
            sparams.setMargins(
                    prop.uname_marginLeft,
                    prop.uname_marginTop,
                    prop.uname_marginRight,
                    prop.uname_marginBottom);
        }

        if (prop.common_background != null) baseView.setBackground(prop.common_background);
        if (prop.msg_bubble != null) uiMessageText.setBackground(prop.msg_bubble);
        if (prop.msg_maxWidth != -1) uiMessageText.setMaxWidth(prop.msg_maxWidth);
        if (prop.msg_textColor != -1) uiMessageText.setTextColor(prop.msg_textColor);
        uiMessageText.setPadding(
                prop.msg_paddingLeft,
                prop.msg_paddingTop,
                prop.msg_paddingRight,
                prop.msg_paddingBottom);
    }

    @Override
    protected MMXTextProperty onReadAttributes(AttributeSet attrs) {
        TypedArray arr = readTypedArray(attrs, R.styleable.DefaultMMXMessageView);
        try {
            MMXTextProperty props = new MMXTextProperty();
            props.letters_textSize = arr.getDimensionPixelSize(R.styleable.DefaultMMXMessageView_letters_textSize, R.dimen.text_16);
            props.letters_textColor = arr.getColor(R.styleable.DefaultMMXMessageView_letters_textColor, -1);

            props.upic_src = arr.getDrawable(R.styleable.DefaultMMXMessageView_upic_src);
            props.upic_height = arr.getDimensionPixelSize(R.styleable.DefaultMMXMessageView_upic_height, -1);
            props.upic_width = arr.getDimensionPixelSize(R.styleable.DefaultMMXMessageView_upic_width, -1);
            props.upic_borderSize = arr.getDimensionPixelSize(R.styleable.DefaultMMXMessageView_upic_borderSize, -1);
            props.upic_borderColor = arr.getColor(R.styleable.DefaultMMXMessageView_upic_borderColor, -1);
            props.upic_marginLeft = arr.getDimensionPixelSize(R.styleable.DefaultMMXMessageView_upic_marginLeft, 0);
            props.upic_marginRight = arr.getDimensionPixelSize(R.styleable.DefaultMMXMessageView_upic_marginRight, 0);
            props.upic_marginTop = arr.getDimensionPixelSize(R.styleable.DefaultMMXMessageView_upic_marginTop, 0);
            props.upic_marginBottom = arr.getDimensionPixelSize(R.styleable.DefaultMMXMessageView_upic_marginBottom, 0);

            props.date_textColor = arr.getColor(R.styleable.DefaultMMXMessageView_date_textColor, -1);
            props.date_textSize = arr.getDimensionPixelSize(R.styleable.DefaultMMXMessageView_date_textSize, -1);

            props.uname_textSize = arr.getDimensionPixelSize(R.styleable.DefaultMMXMessageView_uname_textSize, -1);
            props.uname_textColor = arr.getColor(R.styleable.DefaultMMXMessageView_uname_textColor, 0);
            props.uname_marginLeft = arr.getDimensionPixelSize(R.styleable.DefaultMMXMessageView_uname_marginLeft, getDimensAsPixel(R.dimen.dimen_10));
            props.uname_marginRight = arr.getDimensionPixelSize(R.styleable.DefaultMMXMessageView_uname_marginRight, getDimensAsPixel(R.dimen.dimen_10));
            props.uname_marginTop = arr.getDimensionPixelSize(R.styleable.DefaultMMXMessageView_uname_marginTop, 0);
            props.uname_marginBottom = arr.getDimensionPixelSize(R.styleable.DefaultMMXMessageView_uname_marginBottom, 0);

            props.common_background = arr.getDrawable(R.styleable.DefaultMMXMessageView_common_background);

            props.msg_bubble = arr.getDrawable(R.styleable.DefaultMMXMessageView_msg_bubble);
            props.msg_maxWidth = arr.getDimensionPixelSize(R.styleable.DefaultMMXMessageView_msg_maxWidth, -1);
            props.msg_textColor = arr.getColor(R.styleable.DefaultMMXMessageView_msg_textColor, -1);
            props.msg_paddingLeft = arr.getDimensionPixelSize(R.styleable.DefaultMMXMessageView_msg_paddingLeft, getDimensAsPixel(R.dimen.dimen_15));
            props.msg_paddingTop = arr.getDimensionPixelSize(R.styleable.DefaultMMXMessageView_msg_paddingTop, 0);
            props.msg_paddingRight = arr.getDimensionPixelSize(R.styleable.DefaultMMXMessageView_msg_paddingRight, getDimensAsPixel(R.dimen.dimen_15));
            props.msg_paddingBottom = arr.getDimensionPixelSize(R.styleable.DefaultMMXMessageView_msg_paddingBottom, 0);

            return props;
        } finally {
            arr.recycle();
        }
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
    public void onShowUserPicture(String url, String name) {
        onSetUserPicOrLetters(url, name);
    }

    @Override
    public void onSenderName(String name) {
        if (uiSenderName != null) uiSenderName.setText(name);
    }

    @Override
    protected MMXMessageContract.Presenter readPresenter(MMXMessagePresenterFactory factory) {
        return factory.createMessagePresenter(this);
    }

}
