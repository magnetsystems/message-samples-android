package com.magnet.magnetchat.ui.views.poll;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.magnet.magnetchat.R;

/**
 * Created by aorehov on 17.05.16.
 */
public class DefaultMMXEditAnswerView extends MMXEditAnswerView<EditPollAnswerProperty> {
    private EditText uiAnswer;

    public DefaultMMXEditAnswerView(Context context) {
        super(context);
    }

    public DefaultMMXEditAnswerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultMMXEditAnswerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void updateUI(String obj) {
        uiAnswer.removeTextChangedListener(this);
        uiAnswer.setText(obj);
        uiAnswer.addTextChangedListener(this);
    }

    @Override
    protected EditText getEditTextView() {
        return uiAnswer;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_mmxchat_editable_answer;
    }

    @Override
    protected void onLinkingViews(View baseView) {
        uiAnswer = findView(baseView, R.id.mmx_answer);
    }


    @Override
    public void setProperties(EditPollAnswerProperty property) {

    }
}
