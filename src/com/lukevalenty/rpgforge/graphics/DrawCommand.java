package com.lukevalenty.rpgforge.graphics;

import com.lukevalenty.rpgforge.memory.AbstractPooledObject;
import com.lukevalenty.rpgforge.memory.ObjectPool;

public class DrawCommand<T extends DrawCommand<T>> extends AbstractPooledObject<T> {
    public DrawCommand(final ObjectPool<T> objectPool) {
        super(objectPool);
    }
}
