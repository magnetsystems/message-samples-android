package com.magnet.magnetchat.model.converters;

import com.magnet.magnetchat.callbacks.MMXAction;
import com.magnet.magnetchat.helpers.AsyncHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aorehov on 28.04.16.
 */
public abstract class BaseConverter<FROM, TO> {

    private MMXAction<TO> mapper;

    public abstract TO convert(FROM from);

    public <T extends BaseConverter<FROM, TO>> T map(MMXAction<TO> mapper) {
        this.mapper = mapper;
        return (T) this;
    }

    public void convert(final List<FROM> fromList, final MMXAction<List<TO>> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<TO> toList = convert(fromList);
                onResult(toList, callback);
            }
        }).start();
    }

    public void convert(final FROM from, final MMXAction<TO> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TO convert = convert(from);
                onResult(convert, callback);
            }
        }).start();
    }

    private void onResult(final TO to, final MMXAction<TO> callback) {
        AsyncHelper.UI.post(new Runnable() {
            @Override
            public void run() {
                callback.call(to);
            }
        });
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
            for (int index = 0; index < fromList.size(); index++) {
                FROM from = fromList.get(index);
                if (from != null) {
                    TO to = convert(from);

                    if (to != null) {
                        if (mapper != null) mapper.call(to);
                        list.add(to);
                        int indexOf = list.size() - 1;
                        if (indexOf > -1) {
                            TO prev = indexOf == 0 ? null : list.get(indexOf - 1);
                            decorate(prev, to);
                        }
                    }
                }
            }
        } else {
            list = new ArrayList<>(0);
        }
        mapper = null;
        return list;
    }

    protected void decorate(TO prev, TO to) {

    }

    public boolean isEnableDecoration() {
        return false;
    }

}
