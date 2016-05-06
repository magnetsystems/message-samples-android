package com.magnet.magnetchat.model;

import com.magnet.mmx.client.ext.poll.MMXPollOption;

/**
 * Created by aorehov on 27.04.16.
 */
public class MMXPollOptionWrapper extends MMXObjectWrapper<MMXPollOption> {

    private boolean isVoted;
    private boolean isSelectedLocal;

    public MMXPollOptionWrapper(MMXPollOption obj, boolean isVoted) {
        super(obj);
        this.isVoted = isVoted;
        this.isSelectedLocal = isVoted;
    }

    public boolean isVoted() {
        return isVoted;
    }

    public boolean isSelectedLocal() {
        return isSelectedLocal;
    }

    public void setSelectedLocal(boolean selectedLocal) {
        isSelectedLocal = selectedLocal;
    }

    public String getId() {
        return obj.getOptionId();
    }
}
