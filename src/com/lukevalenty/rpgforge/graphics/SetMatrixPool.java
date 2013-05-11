package com.lukevalenty.rpgforge.graphics;

import com.lukevalenty.rpgforge.memory.ObjectPool;

public class SetMatrixPool extends ObjectPool<SetMatrix> {
    @Override
    protected SetMatrix create() {
        return new SetMatrix(this);
    }
}
