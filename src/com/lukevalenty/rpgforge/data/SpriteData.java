package com.lukevalenty.rpgforge.data;

import android.graphics.Rect;

public class SpriteData {
    private int frameDelay;
    private SpriteSetData spriteSet;
    private Rect src;

    protected SpriteData() {
        // default constructor needed for serialization
        this.frameDelay = 8;
    }
    
    public SpriteData(
        final SpriteSetData spriteSet,
        final Rect src
    ) {
        this();
        this.spriteSet = spriteSet;
        this.src = src;
    }
}
