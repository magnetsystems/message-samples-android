package com.magnet.magnetchat.ui.views.chatlist.list;

import android.graphics.drawable.Drawable;

import com.magnet.magnetchat.ui.views.abs.ViewProperty;


/**
 * Created by aorehov on 20.05.16.
 */
public abstract class MMXMessageBaseProperty extends ViewProperty {
    int letters_textSize;
    int letters_textColor;

    Drawable upic_src;
    int upic_height;
    int upic_width;
    int upic_borderSize;
    int upic_borderColor;
    int upic_marginLeft;
    int upic_marginRight;
    int upic_marginTop;
    int upic_marginBottom;

//    String date_pattern;
    int date_textColor;
    int date_textSize;

    int uname_textSize;
    int uname_textColor;
    int uname_marginLeft;
    int uname_marginRight;
    int uname_marginTop;
    int uname_marginBottom;

    Drawable common_background;
}
