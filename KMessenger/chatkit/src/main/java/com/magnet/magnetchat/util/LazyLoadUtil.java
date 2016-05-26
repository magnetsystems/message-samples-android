package com.magnet.magnetchat.util;

/**
 * Created by aorehov on 29.04.16.
 */
public class LazyLoadUtil {

    private final OnNeedLoadingCallback loadingCallback;
    private final int pageSize;
    private int offset;
    private boolean isLoading;

    public LazyLoadUtil(int pageSize, int offset, OnNeedLoadingCallback loadingCallback) {
        this.offset = offset;
        this.loadingCallback = loadingCallback;
        this.pageSize = pageSize;
    }

    public final void checkLazyLoad(int summarySize, int localSize, long localPosition) {
        if (isLoading) {
            return;
        }

        if (localSize >= summarySize || localSize < pageSize) {
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
