package com.magnet.magntetchatapp.factories;

import android.support.annotation.NonNull;

import com.magnet.magntetchatapp.callbacks.OnActivityEventCallback;
import com.magnet.magntetchatapp.ui.fragments.abs.BaseFragment;
import com.magnet.magntetchatapp.ui.fragments.section.HomeFragment;

/**
 * Created by Artli_000 on 18.03.2016.
 */
public class FragmentFactory {

    /**
     * Fragment type
     */
    public enum FragmentType {
        HOME
    }

    /**
     * Method which provide the get fragment by type
     *
     * @param type     fragment type
     * @param callback event callback
     * @return fragment
     */
    @NonNull
    public static BaseFragment getFragment(@NonNull FragmentType type, @NonNull OnActivityEventCallback callback) {
        BaseFragment baseFragment;
        switch (type) {
            case HOME:
                baseFragment = new HomeFragment();
                break;
            default:
                baseFragment = new HomeFragment();
                break;
        }

        if (baseFragment != null) {
            baseFragment.setEventCallback(callback);
        }
        return baseFragment;
    }
}
