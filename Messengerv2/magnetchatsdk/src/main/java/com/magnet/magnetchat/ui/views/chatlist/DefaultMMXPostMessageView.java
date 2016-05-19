package com.magnet.magnetchat.ui.views.chatlist;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.ui.views.abs.ViewProperty;

/**
 * Created by aorehov on 04.05.16.
 */
public class DefaultMMXPostMessageView extends MMXPostMessageView<DefaultMMXPostMessageView.MMXPostMessageProperty> {
    private View uiAttachment;
    private EditText uiMessageContent;
    private View uiSendMessage;

    public DefaultMMXPostMessageView(Context context) {
        super(context);
    }

    public DefaultMMXPostMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultMMXPostMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View getUIAttachment() {
        return uiAttachment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_mmxchat_post_message;
    }

    @Override
    protected void onLinkingViews(View baseView) {
        uiAttachment = findView(R.id.mmx_attachments);
        uiMessageContent = findView(R.id.mmx_message_field);
        uiSendMessage = findView(R.id.mmx_send);

        setOnClickListeners(uiAttachment, uiSendMessage);

        updateUI();
    }

    @Override
    protected DefaultMMXPostMessageView.MMXPostMessageProperty onReadAttributes(AttributeSet attrs) {
        TypedArray typed = readTypedArray(attrs, R.styleable.DefaultMMXPostMessageView);
        try {
            MMXPostMessageProperty property = new MMXPostMessageProperty();
            property.text_marginLeft = typed.getDimensionPixelSize(R.styleable.DefaultMMXPostMessageView_text_marginLeft, getResources().getDimensionPixelSize(R.dimen.dimen_3));
            property.text_marginRight = typed.getDimensionPixelSize(R.styleable.DefaultMMXPostMessageView_text_marginRight, 0);
            property.text_marginBottom = typed.getDimensionPixelSize(R.styleable.DefaultMMXPostMessageView_text_marginBottom, 0);
            property.text_marginTop = typed.getDimensionPixelSize(R.styleable.DefaultMMXPostMessageView_text_marginTop, 0);
            property.text_background = typed.getDrawable(R.styleable.DefaultMMXPostMessageView_text_background);
            property.text_hint = typed.getString(R.styleable.DefaultMMXPostMessageView_text_hint);
            property.text_textSize = typed.getDimension(R.styleable.DefaultMMXPostMessageView_text_textSize, getResources().getDimension(R.dimen.text_15));
            property.text_color = typed.getColor(R.styleable.DefaultMMXPostMessageView_text_textColor, -1);
            property.text_lines = typed.getInt(R.styleable.DefaultMMXPostMessageView_text_maxLines, 1);
            return property;
        } finally {
            typed.recycle();
        }
    }

    @Override
    protected void onApplyAttributes(DefaultMMXPostMessageView.MMXPostMessageProperty prop) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) uiMessageContent.getLayoutParams();
        params.setMargins(
                prop.text_marginLeft,
                prop.text_marginTop,
                prop.text_marginRight,
                prop.text_marginBottom);

        uiMessageContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, prop.text_textSize);
        uiMessageContent.setHint(prop.text_hint);

        if (prop.text_color != -1) uiMessageContent.setTextColor(prop.text_color);
        if (prop.text_background != null) uiMessageContent.setBackground(prop.text_background);
        if (prop.text_lines > 1) {
            uiMessageContent.setSingleLine(false);
            uiMessageContent.setMaxLines(prop.text_lines);
        } else {
            uiMessageContent.setMaxLines(1);
            uiMessageContent.setSingleLine(true);
        }
    }

    @Override
    public String getMessageText() {
        return uiMessageContent.getText().toString();
    }

    @Override
    public void onChannelAvailable() {
        uiAttachment.setEnabled(true);
        uiMessageContent.setEnabled(true);
        uiSendMessage.setEnabled(true);
    }

    @Override
    public void onChannelNotAvailable() {
        uiAttachment.setEnabled(false);
        uiMessageContent.setEnabled(false);
        uiSendMessage.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mmx_attachments) {
            onGetAttachment();
        } else if (v.getId() == R.id.mmx_send) {
            getPresenter().sendTextMessage();
            uiMessageContent.setText(null);
        }
    }

    /**
     * Created by aorehov on 04.05.16.
     */
    public class MMXPostMessageProperty extends ViewProperty {
        int text_marginLeft;
        int text_marginRight;
        int text_marginTop;
        int text_marginBottom;
        Drawable text_background;
        String text_hint;
        float text_textSize;
        int text_color;
        int text_lines;
    }


}
