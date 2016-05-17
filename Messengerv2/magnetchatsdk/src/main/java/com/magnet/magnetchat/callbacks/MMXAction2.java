package com.magnet.magnetchat.callbacks;

/**
 * Created by aorehov on 17.05.16.
 */
public interface MMXAction2<T, R> {
    R call(T action);
}
