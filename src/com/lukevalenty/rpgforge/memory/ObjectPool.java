package com.lukevalenty.rpgforge.memory;

import java.util.ArrayList;

import com.google.inject.Provider;

public abstract class ObjectPool<T extends PooledObject<T>> implements Provider<PooledObject<T>> {
    private final ArrayList<T> objectPool;
    
    public ObjectPool() {
        objectPool = new ArrayList<T>();
    }
    
    public T get() {
        if (objectPool.isEmpty()) {
            return create();
            
        } else {
            return objectPool.remove(objectPool.size() - 1);
        }
    }
    
    public void recycle(final T pooledObject) {
        objectPool.add(pooledObject);
    }
    
    protected abstract T create();
}
