package com.lukevalenty.rpgforge.graphics;

import com.lukevalenty.rpgforge.memory.ObjectPool;

public class DrawTilemap extends DrawCommand<DrawTilemap> {
    public DrawTilemap(final ObjectPool<DrawTilemap> objectPool) {
        super(objectPool);
    }
}
