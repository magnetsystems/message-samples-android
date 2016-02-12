package com.magnet.magnetchat.callbacks;

/**
 * Created by Artli_000 on 11.02.2016.
 */
public interface BaseActivityCallback {
    enum Event {
        ACTIVITY_CLOSE
    }

    void onReceiveFragmentEvent(Event event);
}
