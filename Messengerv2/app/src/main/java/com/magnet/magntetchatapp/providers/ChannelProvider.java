package com.magnet.magntetchatapp.providers;

import com.magnet.magnetchat.mvp.api.abs.ChannelsListContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Artli_000 on 18.03.2016.
 */
public class ChannelProvider implements BaseProvider<List<ChannelsListContract.ChannelObject>> {
    @Override
    public List<ChannelsListContract.ChannelObject> get() {
        return new ArrayList<ChannelsListContract.ChannelObject>(Arrays.asList(new ChannelsListContract.ChannelObject[]{
                new ChannelsListContract.ChannelObject(null),
                new ChannelsListContract.ChannelObject(null),
                new ChannelsListContract.ChannelObject(null),
                new ChannelsListContract.ChannelObject(null),
                new ChannelsListContract.ChannelObject(null),
                new ChannelsListContract.ChannelObject(null),
                new ChannelsListContract.ChannelObject(null),
                new ChannelsListContract.ChannelObject(null),
                new ChannelsListContract.ChannelObject(null),
                new ChannelsListContract.ChannelObject(null),
                new ChannelsListContract.ChannelObject(null),
                new ChannelsListContract.ChannelObject(null),
        }));
    }
}
