package com.lukevalenty.rpgforge.data;

import java.util.Iterator;

import android.util.SparseArray;

public class SparseArrayIterator<T> implements Iterator<T> {
    private final SparseArray<T> events;
    private final Class<T> objectType;
    
    private int index = 0;

    public SparseArrayIterator(
        final SparseArray<T> events, 
        final Class<T> objectType
    ) {
        this.events = events;
        this.objectType = objectType;
    }

    @Override
    public boolean hasNext() {
        while (
            index < events.size() && 
            (
                events.valueAt(index) == null || 
                !(objectType.isInstance(events.valueAt(index)))
            )
        ) {
            events.remove(index);
            index++;
        }
        
        return index < events.size();
    }

    @Override
    public T next() {
        return events.valueAt(index++);
    }

    @Override
    public void remove() {
        // do nothing
    }
}