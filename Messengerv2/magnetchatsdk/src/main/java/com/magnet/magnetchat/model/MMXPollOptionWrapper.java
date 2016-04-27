package com.magnet.magnetchat.model;

import com.magnet.mmx.client.ext.poll.MMXPollOption;

/**
 * Created by aorehov on 27.04.16.
 */
public class MMXPollOptionWrapper extends MMXObjectWrapper<MMXPollOption> {

    private final boolean isVoted;

    public MMXPollOptionWrapper(MMXPollOption obj, boolean isVoted) {
        super(obj);
        this.isVoted = isVoted;
    }

    public boolean isVoted() {
        return isVoted;
    }
}
