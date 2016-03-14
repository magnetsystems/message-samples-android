package com.magnet.magnetchat.helpers;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by dlernatovich on 2/29/16.
 */
public class UserInterfaceHelper {

    /**
     * Method which provide the setting of the dimension from the dimension ID
     *
     * @param dimension dimension ID
     * @param textViews text views array
     */
    public static void setControlsTextDimension(Context context, int dimension, TextView... textViews) {
        for (TextView textView : textViews) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(dimension));
        }
    }

    /**
     * Method which provide to create the typeface to views
     *
     * @param typeface  typeface
     * @param textViews views
     */
    public static void setControlsTextTypeface(Typeface typeface, TextView... textViews) {

        if (typeface == null) {
            return;
        }

        for (TextView textView : textViews) {
            textView.setTypeface(typeface);
        }
    }

    /**
     * Method which provide the setting of the text hint color
     *
     * @param textColor text color
     * @param editTexts edit texts
     */
    public static void setEditTextHintColor(int textColor, EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setHintTextColor(textColor);
        }
    }

    /**
     * Method which provide to getting the String value from field
     *
     * @param textView text view field
     * @return
     */
    public static String getStringFromField(TextView textView) {
        if (textView != null) {
            return textView.getText().toString().trim();
        }
        return "";
    }

    /**
     * Method which provide to getting the String value from field
     *
     * @param editText text view field
     * @return
     */
    public static String getStringFromField(EditText editText) {
        if (editText != null) {
            return editText.getText().toString().trim();
        }
        return "";
    }

}
