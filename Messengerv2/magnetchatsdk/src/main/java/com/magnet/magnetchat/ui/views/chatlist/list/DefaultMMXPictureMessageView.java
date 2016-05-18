package com.magnet.magnetchat.ui.views.chatlist.list;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.IntentHelper;
import com.magnet.magnetchat.presenters.chatlist.MMXPicMessageContract;

import java.util.Date;

/**
 * Created by aorehov on 06.05.16.
 */
public abstract class DefaultMMXPictureMessageView extends AbstractMMXPictureMessageView<MMXPictureProperty> {

    ImageView uiPic;

    public DefaultMMXPictureMessageView(Context context) {
        super(context);
    }

    public DefaultMMXPictureMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultMMXPictureMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setProperties(MMXPictureProperty property) {

    }

    @Override
    protected void onLinkingViews(View baseView) {
        super.onLinkingViews(baseView);
        uiPic = findView(R.id.mmx_msg_pic);

        uiPic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mmx_msg_pic) {
            String url = getPresenter().getImageURL();
            if (url != null) {
                Intent intent = IntentHelper.openImage(Uri.parse(url));
                startActivity(intent);
            }
        } else
            super.onClick(v);
    }

    @Override
    protected MMXPicMessageContract.Presenter readPresenter() {
        return ChatSDK.getMMXMessagPresenterFactory().createPicMessagePresenter(this);
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
    public void onShowUserPicture(String url, String name) {
        onSetUserPicOrLetters(url, name);
    }

    @Override
    public void onSenderName(String name) {
        if (uiSenderName != null) uiSenderName.setText(name);
    }

    @Override
    public void onPicture(String url) {
        Glide.with(getContext())
                .load(Uri.parse(url))
                .placeholder(R.drawable.photo_msg)
                .into(uiPic);
    }
}
