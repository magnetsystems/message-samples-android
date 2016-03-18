package com.magnet.magntetchatapp.callbacks;

/**
 * Created by Artli_000 on 18.03.2016.
 */
public interface OnActivityEventCallback {

    /**
     * Event types
     */
    enum Event {
        NONE
    }

    /**
     * Method which provide the event received from fragment
     *
     * @param event   event type
     * @param objects list of objects
     */
    void onEventReceived(Event event, Object... objects);
}
