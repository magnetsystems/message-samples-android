package com.magnet.magnetchat.model.converters.impl;

import com.magnet.magnetchat.model.converters.MMXMessageWrapperConverter;
import com.magnet.magnetchat.model.converters.MMXPollOptionWrapperConverter;
import com.magnet.magnetchat.model.converters.factories.MMXObjectConverterFactory;
import com.magnet.max.android.User;

/**
 * Created by aorehov on 28.04.16.
 */
public class DefaultMMXObjectConverterFactory implements MMXObjectConverterFactory {
    @Override
    public MMXMessageWrapperConverter createMMXMessageConverter() {
        return new DefaultMMXMessageWrapperConverter(User.getCurrentUserId());
    }

    @Override
    public MMXPollOptionWrapperConverter createMmxPollOptionWrapperConverter() {
        return new DefaultMMXPollOptionWrapperConverter();
    }
}
