package com.magnet.magnetchat.ui.views;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.ui.views.abs.BaseView;

import butterknife.InjectView;

public class EventView extends BaseView {

    @InjectView(R.id.tvPrimarySubscribers)
    AppCompatTextView tvPrimarySubscribers;

    public EventView(Context context) {
        super(context);
    }

    public EventView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EventView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_event;
    }

    @Override
    protected void onCreateView() {

    }

    /**
     * @param amount size of array with active channel subscribers
     */
    public void setSubscribersAmount(int amount) {
        tvPrimarySubscribers.setText(String.format("%d Subscribers", amount));
    }

}
