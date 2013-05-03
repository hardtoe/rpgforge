package com.lukevalenty.rpgforge.graphics;

import com.google.inject.Singleton;
import com.lukevalenty.rpgforge.memory.ObjectPool;

@Singleton
public class DrawSpritePool extends ObjectPool<DrawSprite> {
    @Override
    public DrawSprite create() {
        return new DrawSprite(this);
    }
}
