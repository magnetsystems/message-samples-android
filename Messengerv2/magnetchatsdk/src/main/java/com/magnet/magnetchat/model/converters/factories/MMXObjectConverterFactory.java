package com.magnet.magnetchat.model.converters.factories;

import com.magnet.magnetchat.model.converters.MMXMessageWrapperConverter;
import com.magnet.magnetchat.model.converters.MMXPollOptionWrapperConverter;

/**
 * Created by aorehov on 28.04.16.
 */
public interface MMXObjectConverterFactory {

    MMXMessageWrapperConverter createMMXMessageConverter();

    MMXPollOptionWrapperConverter createMmxPollOptionWrapperConverter();
}
