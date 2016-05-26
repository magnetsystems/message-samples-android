package com.magnet.magnetchat.ui.views.chatlist.list;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.MMXObjectsHelper;
import com.magnet.magnetchat.model.MMXMessageWrapper;
import com.magnet.magnetchat.model.converters.BaseConverter;
import com.magnet.magnetchat.presenters.chatlist.BaseMMXMessagePresenter;
import com.magnet.magnetchat.presenters.chatlist.MMXMessagePresenterFactory;
import com.magnet.magnetchat.ui.views.abs.BaseMMXTypedView;
import com.magnet.magnetchat.ui.views.abs.ViewProperty;
import com.magnet.magnetchat.util.Logger;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by aorehov on 05.05.16.
 */
public abstract class BaseMMXMessageView<T extends MMXMessageBaseProperty, P extends BaseMMXMessagePresenter> extends BaseMMXTypedView<MMXMessageWrapper, T> {

    TextView uiUserLetters;
    CircleImageView uiUserPicView;
    TextView uiDate;
    TextView uiSenderName;

    private Drawable defColorUserPic;

    private BaseConverter<Date, String> dateConverter;

    private P presenter;
    private boolean isLoadPicture;

    public BaseMMXMessageView(Context context) {
        super(context);
    }

    public BaseMMXMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseMMXMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDateConverter(BaseConverter<Date, String> dateConverter) {
        this.dateConverter = dateConverter;
    }

    public BaseConverter<Date, String> getDateConverter() {
        if (this.dateConverter == null) {
            this.dateConverter = ChatSDK.getMmxObjectConverterFactory().createMessageDateConverterFactory();
        }
        return dateConverter;
    }

    public void setDefColorUserPic(Drawable defColorUserPic) {
        this.defColorUserPic = defColorUserPic;
        uiUserPicView.setImageDrawable(defColorUserPic);
    }

    @Override
    protected void onLinkingViews(View baseView) {
        defColorUserPic = new ColorDrawable(getColor(R.color.accent));
        uiUserLetters = findView(baseView, R.id.mmx_user_name_letters);
        uiUserPicView = findView(baseView, R.id.mmx_msg_pic_origin);
        uiDate = findView(baseView, R.id.mmx_msg_date);
        uiSenderName = findView(baseView, R.id.mmx_sender);
    }

    @Override
    protected void onApplyAttributes(T prop) {
        super.onApplyAttributes(prop);
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
    }

    protected void onSetUserPicOrLetters(final String picUrl, final String name) {
        isLoadPicture = false;
        setLetters(name);
        if (picUrl != null && picUrl.length() > 5) {
            isLoadPicture = true;
            Glide.with(getContext())
                    .load(picUrl)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            Logger.error(getClass().getSimpleName(), e, picUrl);
                            setLetters(name);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            if (isLoadPicture) {
                                uiUserLetters.setVisibility(GONE);
                                uiUserPicView.setImageDrawable(resource);
                                isLoadPicture = false;
                            }
                            return true;
                        }
                    }).preload();
//                    .into(uiUserPicView);
        }
    }

    private void setLetters(String name) {
        String lettersFromName = MMXObjectsHelper.getLettersFromName(name);
        uiUserPicView.setImageDrawable(defColorUserPic);
        uiUserLetters.setText(lettersFromName);
        uiUserLetters.setVisibility(VISIBLE);
    }

    protected void setDate(Date date) {
        if (date == null) {
            uiDate.setVisibility(GONE);
            Logger.error("Message", "empty date");
        } else {
            String dateText = getDateConverter().convert(date);
            if (dateText == null) {
                uiDate.setVisibility(GONE);
                Logger.error("Message", "can't convert date");
            } else {
                uiDate.setText(dateText);
                uiDate.setVisibility(VISIBLE);
            }
        }
    }

    @Override
    protected void onCreateView() {
        presenter = readPresenter(getMessagePresenterFactory(getPresenterFactoryName()));
    }

    public P getPresenter() {
        return presenter;
    }

    @Override
    public void setObject(MMXMessageWrapper object) {
        super.setObject(object);
        presenter.setMMXMessage(object);
    }

    protected abstract P readPresenter(MMXMessagePresenterFactory factory);

    protected String getPresenterFactoryName() {
        return null;
    }

    protected MMXMessagePresenterFactory getMessagePresenterFactory(String name) {
        Object byName = ChatSDK.getMMXFactotyByName(name);
        if (byName != null && byName instanceof MMXMessagePresenterFactory) {
            return (MMXMessagePresenterFactory) byName;
        }
        return ChatSDK.getMMXMessagPresenterFactory();
    }
}
