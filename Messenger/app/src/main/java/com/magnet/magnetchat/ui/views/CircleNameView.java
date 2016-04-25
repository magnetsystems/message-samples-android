package com.magnet.magnetchat.ui.views;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.ui.views.abs.BaseView;

import butterknife.InjectView;

/**
 * Created by dlernatovich on 2/12/16.
 */
public class CircleNameView extends BaseView {

    @InjectView(R.id.textUserName)
    AppCompatTextView labelUserName;

    public CircleNameView(Context context) {
        super(context);
    }

    public CircleNameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleNameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_circle_name;
    }

    @Override
    protected void onCreateView() {

    }

    /**
     * Method which provide the setting of the user name
     *
     * @param text current user name
     */
    public void setText(String text) {
        labelUserName.setText(text);
    }
}
