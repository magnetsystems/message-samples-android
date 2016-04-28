package com.magnet.magnetchat.model.converters.factories;

import com.magnet.magnetchat.model.converters.MMXMessageWrapperConverter;

/**
 * Created by aorehov on 28.04.16.
 */
public interface MMXObjectConverterFactory {

    MMXMessageWrapperConverter createMMXMessageConverter();

}
