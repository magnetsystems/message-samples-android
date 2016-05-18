package com.magnet.magnetchat.ui.views.poll;


import android.content.Context;
import android.util.AttributeSet;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.presenters.MMXCreatePollContract;
import com.magnet.magnetchat.ui.factories.MMXListItemFactory;
import com.magnet.magnetchat.ui.views.abs.BaseView;
import com.magnet.magnetchat.ui.views.abs.ViewProperty;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

/**
 * Created by aorehov on 27.04.16.
 */
public abstract class MMXEditPollView<T extends ViewProperty> extends BaseView<T> implements MMXCreatePollContract.View {

    private OnPollCreatedListener listener;
    private MMXCreatePollContract.Presenter presenter;

    public MMXEditPollView(Context context) {
        super(context);
    }

    public MMXEditPollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MMXEditPollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnPollCreateListener(OnPollCreatedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreateView() {
        presenter = ChatSDK.getPresenterFactory().createMMXCreatePollPresenter(this);
    }

    public void setChannel(MMXChannel mmxChannel) {
        presenter.setMMXChannel(mmxChannel);
    }

    @Override
    public void onPollCreatedSuccess(MMXMessage mmxMessage) {
        if (listener != null) listener.onPollSaveSuccess(mmxMessage);
    }

    @Override
    public void showMessage(CharSequence sequence) {
        toast(sequence);
    }

    @Override
    public void showMessage(int resId, Object... objects) {
        toast(getString(resId, objects));
    }

    @Override
    public void onLock() {
        if (listener != null) listener.onLocked();
    }

    @Override
    public void onUnlock() {
        if (listener != null) listener.onUnlocked();
    }

    protected String itemFactoryName() {
        return null;
    }

    protected MMXListItemFactory createMMXListItemFactory() {
        String name = itemFactoryName();
//        TODO find factory by name here
        return ChatSDK.getMmxListItemFactory();
    }

    public void doCreatePoll() {
        presenter.doCreate();
    }

    public interface OnPollCreatedListener {
        void onLocked();

        void onUnlocked();

        void onPollSaveSuccess(MMXMessage mmxMessage);
    }
}
