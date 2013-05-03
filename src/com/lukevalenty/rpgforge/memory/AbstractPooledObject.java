package com.lukevalenty.rpgforge.memory;

public abstract class AbstractPooledObject<T extends PooledObject<T>> implements PooledObject<T> {
    private final ObjectPool<T> objectPool;
    private boolean allocated;
    
    public AbstractPooledObject(final ObjectPool<T> objectPool) {
        this.objectPool = objectPool;
        this.allocated = false;
    }
    
    protected void init() {
        assert(allocated == false);
        allocated = true;
    }
    
    @SuppressWarnings("unchecked")
    public void recycle() {
        assert(allocated == true);
        objectPool.recycle((T) this);
        allocated = false;
    }
}
