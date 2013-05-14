package com.lukevalenty.rpgforge.data;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;

public abstract class TileData {
    protected TileSetData tileset;
    protected Rect[] src;
    protected int frameDelay;
    protected boolean passable;
    protected int[] frames;
    
    protected int layer = 0;
    
    protected transient int avgColor = 0;
    
    protected TileData() {
        // default constructor needed for serialization
        this.frameDelay = 16;
    }
    
    public TileData(
        final TileSetData tileset,
        final Rect... src
    ) {
        this();
        this.tileset = tileset;
        this.src = src;
    }
    
    public Rect src(long frameIndex) {
        if (frames != null) {

            return src[frames[(int) ((frameIndex / frameDelay) % frames.length)]];
            
        } else {
            return src[(int) ((frameIndex / frameDelay) % src.length)];
        }
    }
    
    public Bitmap bitmap() {
        return tileset.bitmap();
    }
    
    public TileData setFrameDelay(final int delay) {
        this.frameDelay = delay;
        return this;
    }
    
    public TileData setAnimationSequence(final int... frames) {
        this.frames = frames;
        return this;
    }
    
    public TileData setPassable(final boolean passable) {
        this.passable = passable;
        return this;
    }
    
    public TileData setLayer(final int layer) {
        this.layer = layer;
        return this;
    }
    
    public boolean isPassable() {
        return passable;
    }
    
    public Rect getPreview() {
        return src[0];
    }
    
    public int getLayer() {
        return layer;
    }
    
    public int getAvgColor() {
        final int width = src[0].right - src[0].left;
        final int height = src[0].bottom - src[0].top;
        final int numPixels = width * height;
        
        int r = 0;
        int g = 0;
        int b = 0;
        
        if (avgColor == 0) {
            for (int y = src[0].top; y < src[0].bottom; y++) {
                for (int x = src[0].left; x < src[0].right; x++) {
                    int pixel = 
                        bitmap().getPixel(x, y);
                    
                    r += (pixel >> 16) & 0xff;
                    g += (pixel >> 8) & 0xff;
                    b += (pixel) & 0xff;
                }
            }
            
            avgColor = Color.rgb(r / numPixels, g / numPixels, b / numPixels);
        }
        
        return avgColor;
    }
}
