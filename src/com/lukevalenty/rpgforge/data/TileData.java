package com.lukevalenty.rpgforge.data;

import android.graphics.Bitmap;
import android.graphics.Rect;

public abstract class TileData {
    protected TileSetData tileset;
    protected Rect[] src;
    protected int frameDelay;
    protected boolean passable;
    protected int[] frames;
    
    protected int layer = 0;
    
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
}
