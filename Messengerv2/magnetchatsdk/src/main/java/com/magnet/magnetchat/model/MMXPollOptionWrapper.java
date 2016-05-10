package com.magnet.magnetchat.model;

import com.magnet.mmx.client.ext.poll.MMXPollOption;

/**
 * Created by aorehov on 27.04.16.
 */
public class MMXPollOptionWrapper extends MMXObjectWrapper<MMXPollOption> implements Typed {

    public static final int TYPE_POLL_ITEM_MY = 0xFF84;
    public static final int TYPE_POLL_ITEM_ANOTHER = 0x7F89;

    private boolean isVoted;
    private boolean isSelectedLocal;
    private int type;

    public MMXPollOptionWrapper(MMXPollOption obj, boolean isVoted, int type) {
        super(obj);
        this.isVoted = isVoted;
        this.isSelectedLocal = isVoted;
        this.type = type;
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

    public long getVotesCount() {
        return obj.getCount();
    }

    public String getVoteText() {
        return obj.getText();
    }

    public String getId() {
        return obj.getOptionId();
    }

    @Override
    public int getType() {
        return type;
    }
}
