package com.magnet.magnetchat.model;

/**
 * Created by aorehov on 27.04.16.
 */
public class MMXObjectWrapper<T> implements Typed {
    T obj;
    int type;

    public MMXObjectWrapper(T obj, int type) {
        this.obj = obj;
        this.type = type;
    }

    public T getObj() {
        return obj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MMXObjectWrapper<?> that = (MMXObjectWrapper<?>) o;

        if (type != that.type) return false;
        return obj.equals(that.obj);

    }

    @Override
    public int hashCode() {
        return obj.hashCode();
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return obj.toString();
    }
}
