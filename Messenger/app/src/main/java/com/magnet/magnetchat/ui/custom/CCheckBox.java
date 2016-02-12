package com.magnet.magnetchat.ui.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.magnet.magnetchat.core.managers.TypeFaceManager;

/**
 * Created by dlernatovich on 2/9/16.
 */
public class CCheckBox extends CheckBox {
    public CCheckBox(Context context) {
        super(context);
        setTextColor();
    }

    public CCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTextColor();
    }

    public CCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTextColor();
    }

    private void setTextColor() {
        if (this.isInEditMode()) {
            return;
        }
        setTextColor(Color.BLACK);
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
}
