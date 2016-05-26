package com.magnet.magnetchat.model.converters.impl;

import com.magnet.magnetchat.model.MMXPollOptionWrapper;
import com.magnet.magnetchat.model.converters.BaseConverter;
import com.magnet.mmx.client.ext.poll.MMXPoll;
import com.magnet.mmx.client.ext.poll.MMXPollOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aorehov on 06.05.16.
 */
public class DefaultMMXPollOptionWrapperConverter extends BaseConverter<MMXPoll, List<MMXPollOptionWrapper>> {

    private boolean isMine;

    public void setMine(boolean mine) {
        isMine = mine;
    }

    @Override
    public List<MMXPollOptionWrapper> convert(MMXPoll mmxPoll) {
        if (mmxPoll == null || mmxPoll.getOptions() == null) {
            return null;
        }

        List<MMXPollOption> options = mmxPoll.getOptions();
        List<MMXPollOption> selectedByUser = mmxPoll.getMyVotes();

        List<MMXPollOptionWrapper> wrappers = new ArrayList<>(options.size());
        for (MMXPollOption opt : options) {
            wrappers.add(new MMXPollOptionWrapper(opt, isContain(selectedByUser, opt)
                    , isMine ? MMXPollOptionWrapper.TYPE_POLL_ITEM_MY : MMXPollOptionWrapper.TYPE_POLL_ITEM_ANOTHER));
        }
        return wrappers;
    }

    private boolean isContain(List<MMXPollOption> list, MMXPollOption opt) {
        return list == null ? false : list.contains(opt);
    }
}
