package com.magnet.magnetchat.core.managers;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.ref.WeakReference;

/**
 * manager which provide to adding of the custom font inside the application
 * Created by dlernatovich on 12/1/15.
 */
public class TypeFaceManager {

    private static TypeFaceManager instance;
    private final WeakReference<Context> contextWeakReference;

    private final Typeface FONT_BARRIOL;
    private final Typeface FONT_BARRIOL_BOLD;

    /**
     * private constructor
     */
    private TypeFaceManager(Context context) {
        FONT_BARRIOL = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto.ttf");
        FONT_BARRIOL_BOLD = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto_Bold.ttf");
        contextWeakReference = new WeakReference<Context>(context);
    }

    /**
     * Method which provide the getting of the current instance of the singleton (should be used only in application sigleton)
     *
     * @param context current context
     * @return
     */
    public static TypeFaceManager getInstance(Context context) {
        if (instance == null) {
            instance = new TypeFaceManager(context);
        }
        return instance;
    }

    /**
     * Method which provide the getting of the current instance of the singleton
     *
     * @return
     */
    public static TypeFaceManager getInstance() {
        return instance;
    }

    /**
     * Method which provide the getting of the FONT_BARRIOL font normal
     *
     * @return
     */
    public Typeface getBarriolFont() {
        return FONT_BARRIOL;
    }

    /**
     * Method which provide to getting of the FONT_BARRIOL_BOLD
     *
     * @return
     */
    public Typeface getBarriolBoldFont() {
        return FONT_BARRIOL_BOLD;
    }
}
