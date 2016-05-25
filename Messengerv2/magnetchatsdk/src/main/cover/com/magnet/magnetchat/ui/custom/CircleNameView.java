package com.magnet.magnetchat.ui.custom;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.ui.views.abs.BaseCoverView;


/**
 * Created by dlernatovich on 2/12/16.
 */
public class CircleNameView extends BaseCoverView {

    //Using if user name is not available
    static final String K_NOT_AVAILABLE_VALUE = "NA";

    private AppCompatTextView labelUserName;

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
        return R.layout.view_circle_name_cover;
    }

    @Override
    protected void onLinkInterface() {
        labelUserName = (AppCompatTextView) baseView.findViewById(R.id.textUserName);
    }

    @Override
    protected void onCreateView() {

    }

    /**
     * Method which provide the setting of the user name
     *
     * @param userName current user name
     */
    public void setUserName(String userName) {
        StringBuilder builder = new StringBuilder();
        if ((userName != null) && (userName.isEmpty() == false)) {
            userName.trim();
            String[] nameArray = userName.split(" ");
            for (String name : nameArray) {
                if (name.length() > 0) {
                    builder.append(name.charAt(0));
                }
            }
        }
        if (builder.toString().isEmpty() == true) {
            labelUserName.setText(K_NOT_AVAILABLE_VALUE);
        } else {
            labelUserName.setText(builder.toString().trim());
        }
    }
}
