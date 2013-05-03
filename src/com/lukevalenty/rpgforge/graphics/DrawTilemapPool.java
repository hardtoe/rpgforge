package com.lukevalenty.rpgforge.graphics;

import com.google.inject.Singleton;
import com.lukevalenty.rpgforge.memory.ObjectPool;

@Singleton
public class DrawTilemapPool extends ObjectPool<DrawTilemap> {
    @Override
    public DrawTilemap create() {
        return new DrawTilemap(this);
    }
}
