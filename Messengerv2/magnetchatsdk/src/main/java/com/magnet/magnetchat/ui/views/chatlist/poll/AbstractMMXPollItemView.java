package com.magnet.magnetchat.ui.views.chatlist.poll;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.magnet.magnetchat.model.MMXPollOptionWrapper;
import com.magnet.magnetchat.ui.views.abs.BaseMMXTypedView;
import com.magnet.magnetchat.ui.views.abs.ViewProperty;

/**
 * Created by aorehov on 10.05.16.
 */
public abstract class AbstractMMXPollItemView<T extends ViewProperty> extends BaseMMXTypedView<MMXPollOptionWrapper, T> {

    private OnPollItemClickListener listener;

    public AbstractMMXPollItemView(Context context) {
        super(context);
    }

    public AbstractMMXPollItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractMMXPollItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        View v = getClickableView();
        v.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && getObject() != null) listener.onClicked(getObject());
            }
        });
    }

    protected abstract View getClickableView();

    @Override
    public void setObject(MMXPollOptionWrapper object) {
        super.setObject(object);
        if (object != null) {
            updateUI(object);
        } else {
            updateEmptyUI();
        }

    }

    protected void updateEmptyUI() {
//        TODO do something on empty entity
    }

    protected void updateUI(MMXPollOptionWrapper object) {
        if (object == null) {
            return;
        }

        updateCount(object.getVotesCount());
        updateText(object.getVoteText());
        updateIsSelected(object.isSelectedLocal());

    }

    protected abstract void updateIsSelected(boolean selectedLocal);

    protected abstract void updateText(String voteText);

    protected abstract void updateCount(long votesCount);

    public void setListener(OnPollItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnPollItemClickListener {
        void onClicked(MMXPollOptionWrapper wrapper);
    }
}
