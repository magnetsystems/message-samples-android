package com.magnet.magnetchat.model.converters;

import com.magnet.magnetchat.callbacks.MMXAction;
import com.magnet.magnetchat.helpers.AsyncHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aorehov on 28.04.16.
 */
public abstract class BaseConverter<FROM, TO> {

    public abstract TO convert(FROM from);

    public void convert(final List<FROM> fromList, final MMXAction<List<TO>> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<TO> toList = convert(fromList);
                onResult(toList, callback);
            }
        }).start();
    }

    private void onResult(final List<TO> toList, final MMXAction<List<TO>> callback) {
        AsyncHelper.UI.post(new Runnable() {
            @Override
            public void run() {
                callback.call(toList);
            }
        });
    }

    public List<TO> convert(List<FROM> fromList) {
        List<TO> list;
        if (fromList != null) {
            list = new ArrayList<>(fromList.size());
            for (FROM from : fromList) {
                if (from != null) {
                    TO to = convert(from);
                    if (to != null) list.add(to);
                }
            }
        } else {
            list = new ArrayList<>(0);
        }
        return list;
    }

}
