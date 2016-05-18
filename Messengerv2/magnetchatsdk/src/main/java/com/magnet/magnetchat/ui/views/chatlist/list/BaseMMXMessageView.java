package com.magnet.magnetchat.ui.views.chatlist.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.model.MMXMessageWrapper;
import com.magnet.magnetchat.presenters.chatlist.BaseMMXMessagePresenter;
import com.magnet.magnetchat.presenters.chatlist.MMXMessagePresenterFactory;
import com.magnet.magnetchat.ui.views.abs.BaseMMXTypedView;
import com.magnet.magnetchat.ui.views.abs.ViewProperty;
import com.magnet.magnetchat.ui.views.section.chat.CircleNameView;
import com.magnet.magnetchat.util.Logger;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by aorehov on 05.05.16.
 */
public abstract class BaseMMXMessageView<T extends ViewProperty, P extends BaseMMXMessagePresenter> extends BaseMMXTypedView<MMXMessageWrapper, T> {

    CircleNameView uiLettersView;
    CircleImageView uiUserPicView;
    TextView uiDate;
    TextView uiSenderName;

    private P presenter;

    public BaseMMXMessageView(Context context) {
        super(context);
    }

    public BaseMMXMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseMMXMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLinkingViews(View baseView) {
        uiLettersView = findView(baseView, R.id.mmx_msg_pic_letters);
        uiUserPicView = findView(baseView, R.id.mmx_msg_pic_origin);
        uiDate = findView(baseView, R.id.mmx_msg_date);
        uiSenderName = findView(baseView, R.id.mmx_sender);
    }

    protected void onSetUserPicOrLetters(final String picUrl, final String name) {
        setLetters(name);
        if (picUrl != null && picUrl.length() > 5) {
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
                            uiLettersView.setVisibility(GONE);
                            uiUserPicView.setVisibility(VISIBLE);
                            return false;
                        }
                    }).into(uiUserPicView);
        }
    }

    private void setLetters(String name) {
        uiLettersView.setUserName(name);
        uiUserPicView.setVisibility(INVISIBLE);
        uiLettersView.setVisibility(VISIBLE);
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
        if (byName != null) {
            return (MMXMessagePresenterFactory) byName;
        }
        return ChatSDK.getMMXMessagPresenterFactory();
    }
}
