package com.magnet.magnetchat.ui.views.chatlist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.magnet.magnetchat.R;

/**
 * Created by aorehov on 04.05.16.
 */
public class DefaultMMXPostMessageView extends MMXPostMessageView<MMXPostMessageProperty> {
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
    public void setProperties(MMXPostMessageProperty property) {

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
        }
    }

}
