package com.magnet.magnetchat.model;

/**
 * Created by aorehov on 27.04.16.
 */
public class MMXObjectWrapper<T> {
    T obj;

    public MMXObjectWrapper(T obj) {
        this.obj = obj;
    }

    public T getObj() {
        return obj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MMXObjectWrapper<?> that = (MMXObjectWrapper<?>) o;

        return obj.equals(that.obj);

    }

    @Override
    public int hashCode() {
        return obj.hashCode();
    }
}
