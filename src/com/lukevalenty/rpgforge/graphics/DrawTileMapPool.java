package com.lukevalenty.rpgforge.graphics;

import com.lukevalenty.rpgforge.memory.ObjectPool;

public class DrawTileMapPool extends ObjectPool<DrawTileMap> {
    @Override
    protected DrawTileMap create() {
        return new DrawTileMap(this);
    }
}
