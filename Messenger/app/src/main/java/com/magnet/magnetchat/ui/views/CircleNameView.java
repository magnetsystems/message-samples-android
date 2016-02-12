package com.magnet.magnetchat.ui.views;

import android.content.Context;
import android.util.AttributeSet;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.ui.custom.FTextView;
import com.magnet.magnetchat.ui.views.abs.BaseView;

import butterknife.InjectView;

/**
 * Created by dlernatovich on 2/12/16.
 */
public class CircleNameView extends BaseView {

    @InjectView(R.id.textUserName)
    FTextView labelUserName;

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
     * @param userName current user name
     */
    public void setUserName(String userName) {
        StringBuilder builder = new StringBuilder();
        if (userName.isEmpty() == false) {
            userName.trim();
            String[] nameArray = userName.split(" ");
            for (String name : nameArray) {
                if (name.length() > 0) {
                    builder.append(name.charAt(0));
                }
            }
        }
        if (builder.toString().isEmpty() == true) {
            labelUserName.setText("UN");
        } else {
            labelUserName.setText(builder.toString().trim());
        }
    }
}
