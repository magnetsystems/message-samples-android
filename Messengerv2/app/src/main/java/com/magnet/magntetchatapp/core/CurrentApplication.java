package com.magnet.magntetchatapp.core;

import com.magnet.chatsdkcover.core.MagnetMaxApplication;
import com.magnet.magntetchatapp.R;

/**
 * Created by dlernatovich on 3/11/16.
 */
public class CurrentApplication extends MagnetMaxApplication {

    @Override
    protected int getPropertyFile() {
        return R.raw.magnetmax;
    }
}
