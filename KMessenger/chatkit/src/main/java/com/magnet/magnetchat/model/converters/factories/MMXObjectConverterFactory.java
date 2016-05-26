package com.magnet.magnetchat.model.converters.factories;

import com.magnet.magnetchat.model.MMXMessageWrapper;
import com.magnet.magnetchat.model.MMXPollOptionWrapper;
import com.magnet.magnetchat.model.MMXUserWrapper;
import com.magnet.magnetchat.model.converters.BaseConverter;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.ext.poll.MMXPoll;
import com.magnet.mmx.client.ext.poll.MMXPollOption;

import java.util.Date;
import java.util.List;

/**
 * Created by aorehov on 28.04.16.
 */
public interface MMXObjectConverterFactory {

    BaseConverter<MMXMessage, MMXMessageWrapper> createMMXMessageConverter();

    BaseConverter<MMXPoll, List<MMXPollOptionWrapper>> createMmxPollOptionWrapperConverter();

    BaseConverter<User, MMXUserWrapper> createMMXUserConverter();

    BaseConverter<String, MMXPollOption> createMMXPollOptionStringConverter();

    BaseConverter<Date, String> createMessageDateConverterFactory();
}
