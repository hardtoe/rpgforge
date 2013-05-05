package com.lukevalenty.rpgforge.data;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class TileData {
    private TileSetData tileset;
    private Rect[] src;
    private int frameDelay;
    private boolean oscillate;
    private boolean passable;
    private int[] frames;
    
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
    
    public boolean isPassable() {
        return passable;
    }
}
