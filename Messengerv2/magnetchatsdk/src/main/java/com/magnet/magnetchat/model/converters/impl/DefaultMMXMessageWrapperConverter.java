package com.magnet.magnetchat.model.converters.impl;

import com.magnet.magnetchat.helpers.MMXObjectsHelper;
import com.magnet.magnetchat.model.MMXMessageWrapper;
import com.magnet.magnetchat.model.MMXPollMessageWrapper;
import com.magnet.magnetchat.model.converters.MMXMessageWrapperConverter;
import com.magnet.mmx.client.api.MMXMessage;

/**
 * Created by aorehov on 28.04.16.
 */
public class DefaultMMXMessageWrapperConverter extends MMXMessageWrapperConverter {

    private final String userId;

    public DefaultMMXMessageWrapperConverter(String userId) {
        this.userId = userId;
    }

    @Override
    public MMXMessageWrapper convert(MMXMessage mmxMessage) {
        boolean isMine = MMXObjectsHelper.isMyMessage(userId, mmxMessage);

        int type = defineType(mmxMessage, isMine);

        if (type == MMXMessageWrapper.TYPE_POLL_ANOTHER || type == MMXMessageWrapper.TYPE_POLL_MY)
            return new MMXPollMessageWrapper(mmxMessage, isMine);
        else
            return new MMXMessageWrapper(mmxMessage, type, isMine);
    }

    private int defineType(MMXMessage mmxMessage, boolean isMy) {
        return MMXMessageWrapper.defineType(mmxMessage, isMy);
    }
}
