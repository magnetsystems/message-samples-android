package com.magnet.magnetchat.ui.views.abs;

import android.content.Context;
import android.util.AttributeSet;

import com.magnet.magnetchat.model.Typed;

/**
 * Created by aorehov on 28.04.16.
 */
public abstract class BaseMMXTypedView<V extends Typed, T extends ViewProperty> extends BaseView<T> {

    private V object;

    public BaseMMXTypedView(Context context) {
        super(context);
    }

    public BaseMMXTypedView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseMMXTypedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setObject(V object) {
        this.object = object;
    }

    protected V getObject() {
        return object;
    }
}
