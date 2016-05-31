package com.magnet.magnetchat.util;

/**
 * Class helps to do lazy loading for current list
 * Created by aorehov on 29.04.16.
 */
public class LazyLoadUtil {

    /**
     * lazy loading callback
     */
    private final OnNeedLoadingCallback loadingCallback;
    /**
     * lazy loading page size
     */
    private final int pageSize;
    /**
     * offset when need to do lazy loading
     */
    private int offset;
    /**
     * loading state
     */
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
        /**
         * called if need to load data from position
         *
         * @param loadFromPosition
         */
        void onNeedLoad(int loadFromPosition);
    }

}
