package com.magnet.magnetchat.model;

import com.magnet.magnetchat.helpers.MMXObjectsHelper;
import com.magnet.mmx.client.ext.poll.MMXPoll;

import java.util.List;

/**
 * Created by aorehov on 27.04.16.
 */
public class MMXPollWrapper extends MMXObjectWrapper<MMXPoll> {

    private List<MMXPollOptionWrapper> options;

    public MMXPollWrapper(MMXPoll obj) {
        super(obj);
        options = MMXObjectsHelper.convert(obj.getOptions(), obj.getMyVotes());
    }

    public List<MMXPollOptionWrapper> getMMXPollOptions() {
        return options;
    }
}
