package com.magnet.magnetchat.ui.views.chatlist.list;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.IntentHelper;
import com.magnet.magnetchat.presenters.chatlist.MMXMessagePresenterFactory;
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
    protected void onLinkingViews(View baseView) {
        super.onLinkingViews(baseView);
        uiPic = findView(R.id.mmx_msg_pic);

        uiPic.setOnClickListener(this);
    }

    @Override
    protected MMXPictureProperty onReadAttributes(AttributeSet attrs) {
        TypedArray arr = readTypedArray(attrs, R.styleable.DefaultMMXPictureMessageView);
        try {
            MMXPictureProperty props = new MMXPictureProperty();
            props.letters_textSize = arr.getDimensionPixelSize(R.styleable.DefaultMMXPictureMessageView_letters_textSize, R.dimen.text_16);
            props.letters_textColor = arr.getColor(R.styleable.DefaultMMXPictureMessageView_letters_textColor, -1);

            props.upic_src = arr.getDrawable(R.styleable.DefaultMMXPictureMessageView_upic_src);
            props.upic_height = arr.getDimensionPixelSize(R.styleable.DefaultMMXPictureMessageView_upic_height, -1);
            props.upic_width = arr.getDimensionPixelSize(R.styleable.DefaultMMXPictureMessageView_upic_width, -1);
            props.upic_borderSize = arr.getDimensionPixelSize(R.styleable.DefaultMMXPictureMessageView_upic_borderSize, -1);
            props.upic_borderColor = arr.getColor(R.styleable.DefaultMMXPictureMessageView_upic_borderColor, -1);
            props.upic_marginLeft = arr.getDimensionPixelSize(R.styleable.DefaultMMXPictureMessageView_upic_marginLeft, 0);
            props.upic_marginRight = arr.getDimensionPixelSize(R.styleable.DefaultMMXPictureMessageView_upic_marginRight, 0);
            props.upic_marginTop = arr.getDimensionPixelSize(R.styleable.DefaultMMXPictureMessageView_upic_marginTop, 0);
            props.upic_marginBottom = arr.getDimensionPixelSize(R.styleable.DefaultMMXPictureMessageView_upic_marginBottom, 0);

            props.date_textColor = arr.getColor(R.styleable.DefaultMMXPictureMessageView_date_textColor, -1);
            props.date_textSize = arr.getDimensionPixelSize(R.styleable.DefaultMMXPictureMessageView_date_textSize, -1);

            props.uname_textSize = arr.getDimensionPixelSize(R.styleable.DefaultMMXPictureMessageView_uname_textSize, -1);
            props.uname_textColor = arr.getColor(R.styleable.DefaultMMXPictureMessageView_uname_textColor, 0);
            props.uname_marginLeft = arr.getDimensionPixelSize(R.styleable.DefaultMMXPictureMessageView_uname_marginLeft, getDimensAsPixel(R.dimen.dimen_10));
            props.uname_marginRight = arr.getDimensionPixelSize(R.styleable.DefaultMMXPictureMessageView_uname_marginRight, getDimensAsPixel(R.dimen.dimen_10));
            props.uname_marginTop = arr.getDimensionPixelSize(R.styleable.DefaultMMXPictureMessageView_uname_marginTop, 0);
            props.uname_marginBottom = arr.getDimensionPixelSize(R.styleable.DefaultMMXPictureMessageView_uname_marginBottom, 0);

            props.common_background = arr.getDrawable(R.styleable.DefaultMMXPictureMessageView_common_background);

            props.pic_height = arr.getDimensionPixelSize(R.styleable.DefaultMMXPictureMessageView_pic_height, -1);
            props.pic_width = arr.getDimensionPixelSize(R.styleable.DefaultMMXPictureMessageView_pic_width, -1);
            return props;
        } finally {
            arr.recycle();
        }
    }

    @Override
    protected void onApplyAttributes(MMXPictureProperty prop) {
        super.onApplyAttributes(prop);
        ViewGroup.LayoutParams params = uiPic.getLayoutParams();
        if (prop.pic_height != -1) params.height = prop.pic_height;
        if (prop.pic_width != -1) params.width = prop.pic_width;
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
    protected MMXPicMessageContract.Presenter readPresenter(MMXMessagePresenterFactory factory) {
        return factory.createPicMessagePresenter(this);
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
    public void onPicture(String url) {
        if (url != null)
            Glide.with(getContext())
                    .load(Uri.parse(url))
                    .placeholder(R.drawable.photo_msg)
                    .into(uiPic);
    }
}
