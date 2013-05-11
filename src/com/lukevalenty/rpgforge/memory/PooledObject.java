package com.lukevalenty.rpgforge.memory;

public interface PooledObject<T extends PooledObject<T>> {
    public void recycle();
} 
