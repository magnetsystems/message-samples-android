package com.magnet.magnetchat.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.ui.views.abs.BaseView;

import butterknife.InjectView;

/**
 * Shows new messages amount in one category
 */
public class SupportItemView extends BaseView {

    @InjectView(R.id.llMenuItem)
    LinearLayout llMenuCount;
    @InjectView(R.id.tvMenuItemCount)
    TextView countItem;

    public SupportItemView(Context context) {
        super(context);
    }

    public SupportItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SupportItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_numbered_menu;
    }

    @Override
    protected void onCreateView() {

    }

    /**
     * If amount of new messages in category is 0, hides view.
     * In other case shows the number of channels with unread messages
     * @param number
     */
    public void setNumber(int number) {
        if (number == 0) {
            llMenuCount.setVisibility(GONE);
        } else {
            llMenuCount.setVisibility(VISIBLE);
            countItem.setText(String.valueOf(number));
        }
    }

}
