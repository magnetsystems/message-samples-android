package com.magnet.magnetchat.ui.views.chatlist.list;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.IntentHelper;
import com.magnet.magnetchat.helpers.MMXObjectsHelper;
import com.magnet.magnetchat.presenters.chatlist.MMXLocationContract;
import com.magnet.magnetchat.presenters.chatlist.MMXMessagePresenterFactory;
import com.magnet.magnetchat.util.Logger;

import java.util.Date;
import java.util.Locale;

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
        uiPic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mmx_msg_pic) {
            getPresenter().onGetLocation();
        } else
            super.onClick(v);
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
    public void onLocation(double latitude, double longitude) {
        try {
            Intent intent = IntentHelper.showLocation((float) latitude, (float) longitude, 15);
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Logger.error(getClass().getSimpleName(), ex);
            String url = MMXObjectsHelper.getGoogleMapsUrl(latitude, longitude, 18);
            Intent intent = IntentHelper.openLink(url);
            startActivity(intent);
        }
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
    protected MMXLocationContract.Presenter readPresenter(MMXMessagePresenterFactory factory) {
        return factory.createLocationPresenter(this);
    }

    @Override
    public void setProperties(MMXLocationProperty property) {

    }

    @Override
    public void onLocation(String mapUrl) {
        Glide.with(getContext())
                .load(Uri.parse(mapUrl))
                .error(R.drawable.map_msg)
                .into(uiPic);
    }

    @Override
    public void onCantGetLocation() {
        uiPic.setImageResource(R.drawable.map_msg);
    }
}
