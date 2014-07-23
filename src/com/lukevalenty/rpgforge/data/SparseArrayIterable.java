package com.lukevalenty.rpgforge.data;

import java.util.Iterator;

import android.util.SparseArray;

public class SparseArrayIterable<T> implements Iterable<T> {
    private final SparseArray<T> events;
    private final Class<T> objectType;

    public SparseArrayIterable(
        final SparseArray<T> events, 
        final Class<T> objectType
    ) {
        this.events = events;
        this.objectType = objectType;
    }

    @Override
    public Iterator<T> iterator() {
        return new SparseArrayIterator<T>(events, objectType);
    }
}
