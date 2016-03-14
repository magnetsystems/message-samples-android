package com.magnet.magnetchat.ui.views.section.chat;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.UserInterfaceHelper;
import com.magnet.magnetchat.ui.views.abs.BaseView;


/**
 * Created by dlernatovich on 2/12/16.
 */
public class CircleNameView extends BaseView<CircleNameViewProperties> {

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
        return R.layout.view_circle_name;
    }

    @Override
    protected void onLinkingViews(View baseView) {
        labelUserName = (AppCompatTextView) baseView.findViewById(R.id.textUserName);
    }

    @Override
    protected void onCreateView() {

    }

    @Override
    public void setProperties(CircleNameViewProperties property) {
        if (property != null) {
            //Get property
            Typeface typeface = property.getTypeface();
            int textColor = property.getTextColor();
            int textDimmension = property.getTextDimension();
            //Set property
            if (typeface != null) {
                labelUserName.setTypeface(typeface);
            }

            if (isNotDefaultID(textColor)) {
                labelUserName.setTextColor(textColor);
            }

            if (isNotDefaultID(textDimmension)) {
                UserInterfaceHelper.setControlsTextDimension(getContext(), textDimmension, labelUserName);
            }

        }
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
