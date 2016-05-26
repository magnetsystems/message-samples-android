package com.magnet.magnetchat.model.converters.impl;

import com.magnet.magnetchat.model.MMXMessageWrapper;
import com.magnet.magnetchat.model.MMXPollOptionWrapper;
import com.magnet.magnetchat.model.MMXUserWrapper;
import com.magnet.magnetchat.model.converters.BaseConverter;
import com.magnet.magnetchat.model.converters.factories.MMXObjectConverterFactory;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.ext.poll.MMXPoll;
import com.magnet.mmx.client.ext.poll.MMXPollOption;

import java.util.Date;
import java.util.List;

/**
 * Created by aorehov on 28.04.16.
 */
public class DefaultMMXObjectConverterFactory implements MMXObjectConverterFactory {

    private BaseConverter<Date, String> mmxMessageDateConverter;

    public DefaultMMXObjectConverterFactory() {
    }

    @Override
    public BaseConverter<MMXMessage, MMXMessageWrapper> createMMXMessageConverter() {
        return new DefaultMMXMessageWrapperConverter(User.getCurrentUserId());
    }

    @Override
    public BaseConverter<MMXPoll, List<MMXPollOptionWrapper>> createMmxPollOptionWrapperConverter() {
        return new DefaultMMXPollOptionWrapperConverter();
    }

    @Override
    public BaseConverter<User, MMXUserWrapper> createMMXUserConverter() {
        return new DefaultMMXUserConverter();
    }

    @Override
    public BaseConverter<String, MMXPollOption> createMMXPollOptionStringConverter() {
        return new DefaultMMXPollOptionStringConverter();
    }

    @Override
    public BaseConverter<Date, String> createMessageDateConverterFactory() {
        if (mmxMessageDateConverter == null) {
            mmxMessageDateConverter = new DefaultMMXMessageDateConverter();
        }
        return mmxMessageDateConverter;
    }
}
