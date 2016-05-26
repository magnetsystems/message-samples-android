package com.magnet.magnetchat.ui.views.chatlist;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.ui.views.abs.ViewProperty;

/**
 * CUSTOMIZATION EXAMPLE:
 * <p/>
 * app:attach_background="@android:color/holo_red_light"
 * app:attach_marginBottom="0dp"
 * app:attach_marginLeft="0dp"
 * app:attach_marginRight="0dp"
 * app:attach_marginTop="0dp"
 * app:attach_padding="7dp"
 * app:attach_src="@drawable/user_group"
 * app:send_background="@android:color/holo_green_light"
 * app:send_padding="7dp"
 * app:send_text="Post"
 * app:send_textColor="@android:color/holo_red_dark"
 * app:send_textSize="20sp"
 * app:text_background="@android:color/transparent"
 * app:text_hint="Hint text"
 * app:text_marginLeft="@dimen/dimen_7"
 * app:text_marginTop="@dimen/dimen_2"
 * app:text_maxLines="2"
 * app:text_textColor="@android:color/holo_red_dark"
 * app:text_textSize="@dimen/text_14" />
 * <p/>
 * <p/>
 * ======================================================
 * <p/>
 * Created by aorehov on 04.05.16.
 */
public class DefaultMMXPostMessageView extends MMXPostMessageView<DefaultMMXPostMessageView.MMXPostMessageProperty> {
    private ImageView uiAttachment;
    private EditText uiMessageContent;
    private TextView uiSendMessage;

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

            int dimen10 = getResources().getDimensionPixelSize(R.dimen.dimen_10);

            property.attach_padding = typed.getDimensionPixelSize(R.styleable.DefaultMMXPostMessageView_attach_padding, dimen10);
            property.attach_marginLeft = typed.getDimensionPixelSize(R.styleable.DefaultMMXPostMessageView_attach_marginLeft, 0);
            property.attach_marginRight = typed.getDimensionPixelSize(R.styleable.DefaultMMXPostMessageView_attach_marginRight, 0);
            property.attach_marginTop = typed.getDimensionPixelSize(R.styleable.DefaultMMXPostMessageView_attach_marginTop, 0);
            property.attach_marginBottom = typed.getDimensionPixelSize(R.styleable.DefaultMMXPostMessageView_attach_marginBottom, 0);
            property.attach_src = typed.getDrawable(R.styleable.DefaultMMXPostMessageView_attach_src);
            property.attach_background = typed.getDrawable(R.styleable.DefaultMMXPostMessageView_attach_background);

            property.send_padding = typed.getDimensionPixelSize(R.styleable.DefaultMMXPostMessageView_send_padding, dimen10);
            property.send_textColor = typed.getColorStateList(R.styleable.DefaultMMXPostMessageView_send_textColor);
            property.send_textSize = typed.getDimension(R.styleable.DefaultMMXPostMessageView_send_textSize, -1);
            property.send_text = typed.getString(R.styleable.DefaultMMXPostMessageView_send_text);
            property.send_background = typed.getDrawable(R.styleable.DefaultMMXPostMessageView_send_background);

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

//        setup attachment view properties
        int padding = prop.attach_padding;
        uiAttachment.setPadding(padding, padding, padding, padding);
        params = (LinearLayout.LayoutParams) uiAttachment.getLayoutParams();
        params.setMargins(
                prop.attach_marginLeft,
                prop.attach_marginTop,
                prop.attach_marginRight,
                prop.attach_marginBottom);
        if (prop.attach_src != null) uiAttachment.setImageDrawable(prop.attach_src);
        if (prop.attach_background != null) uiAttachment.setBackground(prop.attach_background);

//        setup SEND properties
        padding = prop.send_padding;
        uiSendMessage.setPadding(padding, padding, padding, padding);
        if (prop.send_textColor != null) uiSendMessage.setTextColor(prop.send_textColor);
        if (prop.send_text != null) uiSendMessage.setText(prop.send_text);
        if (prop.send_textSize != -1)
            uiSendMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, prop.send_textSize);
        if (prop.send_background != null) uiSendMessage.setBackground(prop.send_background);

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

        int attach_padding;
        int attach_marginLeft;
        int attach_marginRight;
        int attach_marginTop;
        int attach_marginBottom;
        Drawable attach_src;
        Drawable attach_background;

        int send_padding;
        ColorStateList send_textColor;
        float send_textSize;
        String send_text;
        Drawable send_background;
    }
}
