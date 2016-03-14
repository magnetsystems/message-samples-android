package com.magnet.magnetchat.ui.views.section.chat;

import android.graphics.Typeface;
import com.magnet.magnetchat.ui.views.abs.ViewProperty;

/**
 * Created by dlernatovich on 2/29/16.
 */
public class CircleNameViewProperties extends ViewProperty {
    private Typeface typeface;
    private int textDimension;
    private int textColor;

    public static class PropertyBuilder extends AbstractPropertyBuilder<CircleNameViewProperties> {
        private Typeface typeface;
        private int textDimension;
        private int textColor;

        private PropertyBuilder() {
        }

        public PropertyBuilder addTypeface(Typeface typeface) {
            this.typeface = typeface;
            return this;
        }

        public PropertyBuilder addTextDimension(int textDimension) {
            this.textDimension = textDimension;
            return this;
        }

        public PropertyBuilder addTextColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        @Override
        public CircleNameViewProperties build() {
            CircleNameViewProperties property = new CircleNameViewProperties();
            property.typeface = this.typeface;
            property.textColor = this.textColor;
            property.textDimension = this.textDimension;
            return property;
        }
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public int getTextDimension() {
        return textDimension;
    }

    public int getTextColor() {
        return textColor;
    }
}
