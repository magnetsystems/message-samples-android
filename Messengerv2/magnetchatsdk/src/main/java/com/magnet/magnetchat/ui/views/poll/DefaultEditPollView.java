package com.magnet.magnetchat.ui.views.poll;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.magnet.magnetchat.R;


import java.util.Arrays;
import java.util.List;

/**
 * Created by aorehov on 27.04.16.
 */
public class DefaultEditPollView extends AbstractEditPollView<EditPollProperty> {

    private View uiCreateBtn;
    private CheckBox uiCheckBox;
    private TextView uiQuestion;
    private TextView uiAnswers;

    public DefaultEditPollView(Context context) {
        super(context);
    }

    public DefaultEditPollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultEditPollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onCreateView() {
        super.onCreateView();
        uiCreateBtn = findView(R.id.mmx_create);
        uiCheckBox = findView(R.id.mmx_checkbox);
        uiQuestion = findView(R.id.mmx_poll_question);
        uiAnswers = findView(R.id.mmx_poll_answers);

        uiCreateBtn.setOnClickListener(this);

        setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_poll_edit;
    }

    @Override
    protected void onLinkingViews(View baseView) {

    }

    @Override
    public void setProperties(EditPollProperty property) {
// TODO set properties here
    }

    @Override
    public void onAnswersList(List<String> answers) {
        String join = TextUtils.join(",", answers.toArray());
        uiAnswers.setText(join);
    }

    @Override
    public List<String> getAnswersList() {
        String[] strings = uiAnswers.getText().toString().split(",");
        return Arrays.asList(strings);
    }

    @Override
    public String getQuestion() {
        return uiQuestion.getText().toString();
    }

    @Override
    public boolean isMultipleChoice() {
        return uiCheckBox.isChecked();
    }

    @Override
    public void onLockScreen() {
        uiCreateBtn.setOnClickListener(null);
    }

    @Override
    public void onUnlockScreen() {
        uiCreateBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mmx_create) {
            doSaveAction();
        } else
            super.onClick(v);
    }
}
