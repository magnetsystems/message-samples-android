package com.magnet.magnetchat.ui.views.section.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.MMXObjectsHelper;
import com.magnet.magnetchat.ui.views.abs.BaseView;


/**
 * Created by dlernatovich on 2/12/16.
 */
@Deprecated
public class CircleNameView extends BaseView<CircleNameViewProperties> {

    private TextView labelUserName;

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
        labelUserName = findView(baseView, R.id.textUserName);
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
        String name = MMXObjectsHelper.getLettersFromName(userName);
        labelUserName.setText(name);
    }
}
