package com.magnet.magnetchat.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.ui.views.abs.BaseView;

import butterknife.InjectView;

public class CustomDrawerButton extends BaseView {

    @InjectView(R.id.drawerWarning)
    LinearLayout drawerWarning;

    public CustomDrawerButton(Context context) {
        super(context);
    }

    public CustomDrawerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomDrawerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_drawer_button;
    }

    @Override
    protected void onCreateView() {

    }

    /**
     * Shows exclamation mark under the drawer icon.
     */
    public void showWarning() {
        drawerWarning.setVisibility(VISIBLE);
    }

    /**
     * Hides exclamation mark under the drawer icon.
     */
    public void hideWarning() {
        drawerWarning.setVisibility(GONE);
    }

}
