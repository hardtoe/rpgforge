package com.lukevalenty.rpgforge.graphics;

import com.google.inject.Singleton;
import com.lukevalenty.rpgforge.memory.ObjectPool;

@Singleton
public class DrawTileMapPool extends ObjectPool<DrawTileMap> {
    @Override
    public DrawTileMap create() {
        return new DrawTileMap(this);
    }
}
