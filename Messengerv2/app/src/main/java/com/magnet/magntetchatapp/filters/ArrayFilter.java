package com.magnet.magntetchatapp.filters;

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

    public List<T> applyFilter(@NonNull List<T> list, @Nullable K k) {
        List<T> sortedList = new ArrayList<>();
        for (T t : list) {
            if (compare(t, k)) {
                sortedList.add(t);
            }
        }
        return sortedList;
    }

    public abstract boolean compare(@NonNull T t, @Nullable K k);

}
