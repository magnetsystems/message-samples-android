package com.magnet.magnetchat.util;

/**
 * Created by aorehov on 29.04.16.
 */
public class LazyLoadUtil {

    private final OnNeedLoadingCallback loadingCallback;
    private int offset;
    private boolean isLoading;

    public LazyLoadUtil(int offset, OnNeedLoadingCallback loadingCallback) {
        this.offset = offset;
        this.loadingCallback = loadingCallback;
    }

    public final void checkLazyLoad(int summarySize, int localSize, long localPosition) {
        if (isLoading) {
            return;
        }

        if (localSize >= summarySize) {
            return;
        }

        int stepsToEnd = (int) (localSize - localPosition);

        if (stepsToEnd >= offset) {
            return;
        }

        loadingCallback.onNeedLoad(localSize);


    }

    public final void onLoading() {
        isLoading = true;
    }

    public final void onLoadingFinished() {
        isLoading = false;
    }

    public interface OnNeedLoadingCallback {
        void onNeedLoad(int loadFromPosition);
    }

}
