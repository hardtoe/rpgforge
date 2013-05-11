package com.lukevalenty.rpgforge.graphics;

import com.lukevalenty.rpgforge.memory.ObjectPool;

public class DrawSpritePool extends ObjectPool<DrawSprite> {
    @Override
    protected DrawSprite create() {
        return new DrawSprite(this);
    }
}
