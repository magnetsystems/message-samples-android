package com.magnet.magnetchat.ui.views.chatlist.list;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.IntentHelper;
import com.magnet.magnetchat.helpers.MMXObjectsHelper;
import com.magnet.magnetchat.presenters.chatlist.MMXLocationContract;
import com.magnet.magnetchat.presenters.chatlist.MMXMessagePresenterFactory;
import com.magnet.magnetchat.util.Logger;

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
    protected MMXLocationProperty onReadAttributes(AttributeSet attrs) {
        TypedArray arr = readTypedArray(attrs, R.styleable.DefaultMMXLocMessageView);
        try {
            MMXLocationProperty props = new MMXLocationProperty();
            props.letters_textSize = arr.getDimensionPixelSize(R.styleable.DefaultMMXLocMessageView_letters_textSize, R.dimen.text_16);
            props.letters_textColor = arr.getColor(R.styleable.DefaultMMXLocMessageView_letters_textColor, -1);

            props.upic_src = arr.getDrawable(R.styleable.DefaultMMXLocMessageView_upic_src);
            props.upic_height = arr.getDimensionPixelSize(R.styleable.DefaultMMXLocMessageView_upic_height, -1);
            props.upic_width = arr.getDimensionPixelSize(R.styleable.DefaultMMXLocMessageView_upic_width, -1);
            props.upic_borderSize = arr.getDimensionPixelSize(R.styleable.DefaultMMXLocMessageView_upic_borderSize, -1);
            props.upic_borderColor = arr.getColor(R.styleable.DefaultMMXLocMessageView_upic_borderColor, -1);
            props.upic_marginLeft = arr.getDimensionPixelSize(R.styleable.DefaultMMXLocMessageView_upic_marginLeft, 0);
            props.upic_marginRight = arr.getDimensionPixelSize(R.styleable.DefaultMMXLocMessageView_upic_marginRight, 0);
            props.upic_marginTop = arr.getDimensionPixelSize(R.styleable.DefaultMMXLocMessageView_upic_marginTop, 0);
            props.upic_marginBottom = arr.getDimensionPixelSize(R.styleable.DefaultMMXLocMessageView_upic_marginBottom, 0);

            props.date_textColor = arr.getColor(R.styleable.DefaultMMXLocMessageView_date_textColor, -1);
            props.date_textSize = arr.getDimensionPixelSize(R.styleable.DefaultMMXLocMessageView_date_textSize, -1);

            props.uname_textSize = arr.getDimensionPixelSize(R.styleable.DefaultMMXLocMessageView_uname_textSize, -1);
            props.uname_textColor = arr.getColor(R.styleable.DefaultMMXLocMessageView_uname_textColor, 0);
            props.uname_marginLeft = arr.getDimensionPixelSize(R.styleable.DefaultMMXLocMessageView_uname_marginLeft, getDimensAsPixel(R.dimen.dimen_10));
            props.uname_marginRight = arr.getDimensionPixelSize(R.styleable.DefaultMMXLocMessageView_uname_marginRight, getDimensAsPixel(R.dimen.dimen_10));
            props.uname_marginTop = arr.getDimensionPixelSize(R.styleable.DefaultMMXLocMessageView_uname_marginTop, 0);
            props.uname_marginBottom = arr.getDimensionPixelSize(R.styleable.DefaultMMXLocMessageView_uname_marginBottom, 0);

            props.common_background = arr.getDrawable(R.styleable.DefaultMMXLocMessageView_common_background);

            props.pic_height = arr.getDimensionPixelSize(R.styleable.DefaultMMXLocMessageView_pic_height, -1);
            props.pic_width = arr.getDimensionPixelSize(R.styleable.DefaultMMXLocMessageView_pic_width, -1);
            return props;
        } finally {
            arr.recycle();
        }
    }

    @Override
    protected void onApplyAttributes(MMXLocationProperty prop) {
        super.onApplyAttributes(prop);
        ViewGroup.LayoutParams params = uiPic.getLayoutParams();
        if (prop.pic_height != -1) params.height = prop.pic_height;
        if (prop.pic_width != -1) params.width = prop.pic_width;
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
