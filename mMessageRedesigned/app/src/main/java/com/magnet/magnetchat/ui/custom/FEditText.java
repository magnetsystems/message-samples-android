package com.magnet.magnetchat.ui.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.EditText;

import com.magnet.magnetchat.core.managers.TypeFaceManager;

/**
 * Created by dlernatovich on 2/9/16.
 */
public class FEditText extends EditText {
    public FEditText(Context context) {
        super(context);
        onSetTypeface();
    }

    public FEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        onSetTypeface();
    }

    public FEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onSetTypeface();
    }

    private void onSetTypeface() {

        if (this.isInEditMode()) {
            return;
        }

        Typeface typeface = this.getTypeface();
        if (typeface != null && typeface.isBold()) {
            this.setTypeface(getBoldTypeface());
        } else {
            this.setTypeface(getDefaultTypeface());
        }
    }

    protected Typeface getDefaultTypeface() {
        return TypeFaceManager.getInstance().getBarriolFont();
    }


    protected Typeface getBoldTypeface() {
        return TypeFaceManager.getInstance().getBarriolBoldFont();
    }

    /**
     * Method which provide the formatting text from the HTML value
     *
     * @param formatedText current text
     */
    public void setTextFromHtml(String formatedText) {
        setText(Html.fromHtml(formatedText));
    }

    /**
     * Method which provide the formatting text from the HTML value
     *
     * @param stringId current text ID
     */
    public void setTextFromHtml(int stringId) {
        String formatedText = getContext().getString(stringId);
        setText(Html.fromHtml(formatedText));
    }

    /**
     * Method which provide the setting of the text with null value
     *
     * @param text current text
     */
    public void setSafeText(String text) {
        setText(String.format("%s", text));
    }

    /**
     * Method which provide the getting text from the current component
     *
     * @return current String value
     */
    public String getStringValue() {
        return this.getText().toString().trim();
    }

    /**
     * Method which provide the clearing of the EditText
     */
    public void clear() {
        setText("");
    }

}
