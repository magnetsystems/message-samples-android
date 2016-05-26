package com.magnet.magnetchat.ui.views.abs;

import android.graphics.Typeface;

/**
 * Class which provide the setting up the view through the code
 * Created by dlernatovich on 2/29/16.
 */
public abstract class ViewProperty {
    //Default ID value
    public static final int K_DEFAULT_ID_VALUE = Integer.MIN_VALUE;

    //Typeface property
    protected Typeface typeface;

    public static abstract class AbstractPropertyBuilder<T> {

        //Typeface property
        protected Typeface typeface;

        protected AbstractPropertyBuilder() {
            this.typeface = null;
        }

        public AbstractPropertyBuilder addTypeface(Typeface typeface) {
            this.typeface = typeface;
            return this;
        }

        public abstract T build();
    }

    public Typeface getTypeface() {
        return typeface;
    }
}
