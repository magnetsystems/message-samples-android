package com.magnet.magnetchat.util;

import com.magnet.magnetchat.model.MMXPollOptionWrapper;
import com.magnet.mmx.client.ext.poll.MMXPollOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by aorehov on 27.04.16.
 */
public class MMXPollUtils {

    public List<MMXPollOptionWrapper> convert(List<MMXPollOption> options, List<MMXPollOption> selectedByUser) {
        if (options == null) {
            return Collections.EMPTY_LIST;
        }


        List<MMXPollOptionWrapper> wrappers = new ArrayList<>(options.size());
        for (MMXPollOption opt : options) {
            wrappers.add(new MMXPollOptionWrapper(opt, isContain(selectedByUser, opt)));
        }
        return wrappers;
    }

    private boolean isContain(List<MMXPollOption> list, MMXPollOption opt) {
        return list == null ? false : list.contains(opt);
    }


}
