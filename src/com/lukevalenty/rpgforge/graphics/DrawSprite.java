package com.lukevalenty.rpgforge.graphics;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.lukevalenty.rpgforge.memory.ObjectPool;

public class DrawSprite extends DrawCommand<DrawSprite> {
    public DrawSprite(final ObjectPool<DrawSprite> objectPool) {
        super(objectPool);
        
        src = new Rect();
        dst = new Rect();
    }
    
    private Bitmap texture;
    private final Rect src;
    private final Rect dst;
    
    public DrawSprite set(
        final Bitmap texture,
        final int srcTop,
        final int srcLeft,
        final int srcRight,
        final int srcBottom,
        final int dstTop,
        final int dstLeft,
        final int dstRight,
        final int dstBottom
    ) {
        super.init();
        this.texture = texture;
        
        this.src.top = srcTop;
        this.src.left = srcLeft;
        this.src.right = srcRight;
        this.src.bottom = srcBottom;
        
        this.dst.top = dstTop;
        this.dst.left = dstLeft;
        this.dst.right = dstRight;
        this.dst.bottom = dstBottom;
        
        return this;
    }
    
    public Bitmap texture() {
        return texture;
    }
    
    public Rect src() {
        return src;
    }
    
    public Rect dst() {
        return dst;
    }
    
    @Override
    public void recycle() {
        this.texture = null;
        super.recycle();
    }
}
