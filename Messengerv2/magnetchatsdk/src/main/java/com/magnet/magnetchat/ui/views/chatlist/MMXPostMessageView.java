package com.magnet.magnetchat.ui.views.chatlist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.presenters.PostMMXMessageContract;
import com.magnet.magnetchat.ui.views.abs.BaseView;
import com.magnet.magnetchat.ui.views.abs.ViewProperty;

/**
 * Created by aorehov on 04.05.16.
 */
public abstract class MMXPostMessageView<T extends ViewProperty> extends BaseView<T> implements PostMMXMessageContract.View {
    private PostMMXMessageContract.Presenter presenter;
    private OnAttachmentSelectListener listener;

    public MMXPostMessageView(Context context) {
        super(context);
    }

    public MMXPostMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MMXPostMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onCreateView() {
        presenter = ChatSDK.getPresenterFactory().createPostMessagePresenter(this);
    }

    @Override
    public void showMessage(int resId, Object... objects) {
        toast(getString(resId, objects));
    }

    @Override
    public void showMessage(CharSequence sequence) {
        toast(sequence);
    }

    public PostMMXMessageContract.Presenter getPresenter() {
        return presenter;
    }

    protected void updateUI() {
        View uiAttachment = getUIAttachment();
        if (uiAttachment != null)
            uiAttachment.setVisibility(listener == null ? GONE : VISIBLE);
    }

    protected void onGetAttachment() {
        if (listener != null) listener.onOpenAttachment();
    }

    public void setListener(OnAttachmentSelectListener listener) {
        this.listener = listener;
        updateUI();
    }

    public abstract View getUIAttachment();

    public interface OnAttachmentSelectListener {
        void onOpenAttachment();
    }

}
