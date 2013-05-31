package com.lukevalenty.rpgforge.graphics;

import com.lukevalenty.rpgforge.memory.AbstractPooledObject;
import com.lukevalenty.rpgforge.memory.ObjectPool;

public class DrawCommand<T extends DrawCommand<T>> extends AbstractPooledObject<T> {
    private float z = 0;
    
    public DrawCommand(final ObjectPool<T> objectPool) {
        super(objectPool);
    }
    
    public DrawCommand<T> setZ(final float newZ) {
        this.z = newZ;
        return this;
    }
    
    @Override
    public void init() {
        super.init();
        z = 0;
    }
    
    public float z() {
        return z;
    }
}
