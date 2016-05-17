package com.magnet.magnetchat.model.converters.factories;

import com.magnet.magnetchat.model.converters.MMXMessageWrapperConverter;
import com.magnet.magnetchat.model.converters.MMXPollOptionStringConverter;
import com.magnet.magnetchat.model.converters.MMXPollOptionWrapperConverter;
import com.magnet.magnetchat.model.converters.MMXUserConverter;

/**
 * Created by aorehov on 28.04.16.
 */
public interface MMXObjectConverterFactory {

    MMXMessageWrapperConverter createMMXMessageConverter();

    MMXPollOptionWrapperConverter createMmxPollOptionWrapperConverter();

    MMXUserConverter createMMXUserConverter();

    MMXPollOptionStringConverter createMMXPollOptionStringConverter();
}
