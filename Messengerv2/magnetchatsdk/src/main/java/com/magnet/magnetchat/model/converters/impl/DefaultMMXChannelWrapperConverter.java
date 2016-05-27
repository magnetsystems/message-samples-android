package com.magnet.magnetchat.model.converters.impl;

import com.magnet.magnetchat.model.MMXChannelWrapper;
import com.magnet.magnetchat.model.converters.BaseConverter;
import com.magnet.mmx.client.api.ChannelDetail;

/**
 * Created by aorehov on 27.05.16.
 */
public class DefaultMMXChannelWrapperConverter extends BaseConverter<ChannelDetail, MMXChannelWrapper> {

    @Override
    public MMXChannelWrapper convert(ChannelDetail mmxChannel) {
        return new MMXChannelWrapper(mmxChannel, MMXChannelWrapper.TYPE_CHANNEL);
    }

}
