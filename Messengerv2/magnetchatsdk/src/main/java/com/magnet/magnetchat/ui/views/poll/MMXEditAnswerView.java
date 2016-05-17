package com.magnet.magnetchat.ui.views.poll;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.magnet.magnetchat.model.MMXStringWrapper;
import com.magnet.magnetchat.ui.views.abs.BaseMMXTypedView;
import com.magnet.magnetchat.ui.views.abs.ViewProperty;

/**
 * Created by aorehov on 17.05.16.
 */
public abstract class MMXEditAnswerView<T extends ViewProperty> extends BaseMMXTypedView<MMXStringWrapper, T> implements TextWatcher {
    public MMXEditAnswerView(Context context) {
        super(context);
    }

    public MMXEditAnswerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MMXEditAnswerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setObject(MMXStringWrapper object) {
        super.setObject(object);
        updateUI(object.getObj());
    }

    @Override
    protected void onCreateView() {
        getEditTextView().addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        getObject().setString(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    protected abstract void updateUI(String obj);

    protected abstract EditText getEditTextView();

}
