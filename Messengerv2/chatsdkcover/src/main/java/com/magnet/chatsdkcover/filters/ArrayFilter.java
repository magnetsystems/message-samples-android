package com.magnet.chatsdkcover.filters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dlernatovich on 3/25/16.
 */
public abstract class ArrayFilter<T, K> {

    public ArrayFilter() {
    }

    /**
     * Method which provide the filter applying
     *
     * @param list list of values
     * @param k    compare object
     * @return comparasions results
     */
    public List<T> applyFilter(@NonNull List<T> list, @Nullable K k) {
        List<T> sortedList = new ArrayList<>();
        for (T t : list) {
            if (compare(t, k)) {
                sortedList.add(t);
            }
        }
        return sortedList;
    }

    /**
     * Method which provide the comparing of the object
     *
     * @param t target object
     * @param k comparasions object
     * @return comparasions results
     */
    public abstract boolean compare(@NonNull T t, @Nullable K k);

}
