package com.magnet.magnetchat.model.converters.impl;

import com.magnet.magnetchat.model.MMXUserWrapper;
import com.magnet.magnetchat.model.converters.MMXUserConverter;
import com.magnet.max.android.User;

/**
 * Created by aorehov on 12.05.16.
 */
public class DefaultMMXUserConverter extends MMXUserConverter {
    @Override
    public MMXUserWrapper convert(User user) {
        return new MMXUserWrapper(user, MMXUserWrapper.TYPE_USER);
    }
}
