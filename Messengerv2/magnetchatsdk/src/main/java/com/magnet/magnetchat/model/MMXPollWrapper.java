package com.magnet.magnetchat.model;

import com.magnet.magnetchat.util.MMXPollUtils;
import com.magnet.mmx.client.ext.poll.MMXPoll;

import java.util.List;

/**
 * Created by aorehov on 27.04.16.
 */
public class MMXPollWrapper extends MMXObjectWrapper<MMXPoll> {

    private List<MMXPollOptionWrapper> options;

    public MMXPollWrapper(MMXPoll obj) {
        super(obj);
        MMXPollUtils utils = new MMXPollUtils();
        options = utils.convert(obj.getOptions(), obj.getMyVotes());
    }

    public List<MMXPollOptionWrapper> getMMXPollOptions() {
        return options;
    }
}
