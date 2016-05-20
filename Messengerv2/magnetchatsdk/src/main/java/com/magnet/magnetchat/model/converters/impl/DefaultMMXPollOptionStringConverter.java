package com.magnet.magnetchat.model.converters.impl;

import com.magnet.magnetchat.model.converters.BaseConverter;
import com.magnet.mmx.client.ext.poll.MMXPollOption;

/**
 * Created by aorehov on 17.05.16.
 */
public class DefaultMMXPollOptionStringConverter extends BaseConverter<String, MMXPollOption> {
    @Override
    public MMXPollOption convert(String s) {
        return new MMXPollOption(s);
    }
}
