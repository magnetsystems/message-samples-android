package com.magnet.magnetchat.ui.views.chatlist.list;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.presenters.chatlist.MMXLocationContract;

import java.util.Date;

/**
 * Created by aorehov on 05.05.16.
 */
public abstract class DefaultMMXLocMessageView extends AbstractMMXLocationMessageView<MMXLocationProperty> {
    private ImageView uiPic;

    public DefaultMMXLocMessageView(Context context) {
        super(context);
    }

    public DefaultMMXLocMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultMMXLocMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLinkingViews(View baseView) {
        super.onLinkingViews(baseView);
        uiPic = findView(baseView, R.id.mmx_msg_pic);
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
        uiLettersView.setVisibility(VISIBLE);
        uiUserPicView.setVisibility(GONE);
        uiLettersView.setUserName(letters);
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
    protected MMXLocationContract.Presenter readPresenter() {
        return ChatSDK.getMMXMessagPresenterFactory().createLocationPresenter(this);
    }

    @Override
    public void setProperties(MMXLocationProperty property) {

    }

    @Override
    public void onLocation(String mapUrl) {
        Glide.with(getContext())
                .load(Uri.parse(mapUrl))
                .placeholder(R.drawable.map_msg)
                .into(uiPic);
    }

    @Override
    public void onCantGetLocation() {
        uiPic.setImageResource(R.drawable.map_msg);
    }
}
