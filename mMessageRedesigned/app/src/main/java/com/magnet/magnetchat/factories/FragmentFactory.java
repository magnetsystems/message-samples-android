package com.magnet.magnetchat.factories;

import com.magnet.magnetchat.callbacks.BaseActivityCallback;
import com.magnet.magnetchat.constants.AppFragment;
import com.magnet.magnetchat.ui.fragments.BaseFragment;
import com.magnet.magnetchat.ui.fragments.HomeFragment;

/**
 * Created by Artli_000 on 11.02.2016.
 */
public class FragmentFactory {

    public static BaseFragment getFragment(AppFragment appFragment, BaseActivityCallback baseActivity) {
        BaseFragment baseFragment;
        switch (appFragment) {
            default:
                baseFragment = new HomeFragment();
        }
        baseFragment.setBaseActivityCallback(baseActivity);
        return baseFragment;
    }

}
