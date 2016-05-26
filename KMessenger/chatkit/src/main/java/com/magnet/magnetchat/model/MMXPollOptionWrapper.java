package com.magnet.magnetchat.model;

import com.magnet.mmx.client.ext.poll.MMXPollOption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by aorehov on 27.04.16.
 */
public class MMXPollOptionWrapper extends MMXObjectWrapper<MMXPollOption> {

    public static final int TYPE_POLL_ITEM_MY = 0xFF84;
    public static final int TYPE_POLL_ITEM_ANOTHER = 0x7F89;

    private boolean isVoted;
    private boolean isSelectedLocal;

    public MMXPollOptionWrapper(MMXPollOption obj, boolean isVoted, int type) {
        super(obj, type);
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

    /**
     * ================================================
     * static helpful methods
     * ================================================
     */
    public static boolean isHasChangedState(Collection<MMXPollOptionWrapper> opts) {
        Iterator<MMXPollOptionWrapper> iterator = opts.iterator();
        while (iterator.hasNext()) {
            MMXPollOptionWrapper next = iterator.next();
            if (next.isVoted != next.isSelectedLocal)
                return true;
        }
        return false;
    }

    public static List<MMXPollOption> getVotedOrWithChangedState(Collection<MMXPollOptionWrapper> options) {
        if (options == null) return null;

        ArrayList<MMXPollOption> selectedOpts = new ArrayList<>();
        Iterator<MMXPollOptionWrapper> iterator = options.iterator();
        while (iterator.hasNext()) {
            MMXPollOptionWrapper next = iterator.next();
            if (next.isSelectedLocal) selectedOpts.add(next.getObj());
        }

        return selectedOpts;
    }

    public static List<String> getAnswersAsStringList(Collection<MMXPollOption> options) {
        if (options == null || options.isEmpty()) return Collections.EMPTY_LIST;

        ArrayList<String> strings = new ArrayList<>(options.size());
        Iterator<MMXPollOption> iterator = options.iterator();
        while (iterator.hasNext()) {
            MMXPollOption next = iterator.next();
            strings.add(next.getText());
        }
        return strings;
    }
}
