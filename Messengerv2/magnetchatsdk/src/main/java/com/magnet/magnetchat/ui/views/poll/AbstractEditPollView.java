package com.magnet.magnetchat.ui.views.poll;


import android.content.Context;
import android.util.AttributeSet;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.presenters.PollEditContract;
import com.magnet.magnetchat.ui.views.abs.BaseView;
import com.magnet.magnetchat.ui.views.abs.ViewProperty;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

/**
 * Created by aorehov on 27.04.16.
 */
public abstract class AbstractEditPollView<T extends ViewProperty> extends BaseView<T> implements PollEditContract.View {

    private OnPollCreatedListener listener;
    private PollEditContract.Presenter presenter;

    public AbstractEditPollView(Context context) {
        super(context);
    }

    public AbstractEditPollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractEditPollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setListener(OnPollCreatedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreateView() {
        presenter = ChatSDK.getPresenterFactory().createPollEditPresenter(this);
    }

    public void setChannel(MMXChannel mmxChannel) {
        presenter.setMMXChannel(mmxChannel);
    }

    @Override
    public void onMessage(CharSequence message) {
        toast(message);
    }

    @Override
    public void onMessage(int resId) {
        toast(resId);
    }


    @Override
    public void onPollSaved(MMXMessage mmxMessage) {
        if (listener != null) listener.onPollSaveSuccess(mmxMessage);
    }

    protected void doSaveAction() {
        presenter.doSaveAction();
    }

    public interface OnPollCreatedListener {
        void onPollSaveSuccess(MMXMessage mmxMessage);
    }
}
