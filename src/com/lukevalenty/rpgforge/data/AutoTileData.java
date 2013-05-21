package com.lukevalenty.rpgforge.data;

import java.util.HashSet;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class AutoTileData extends TileData {
    private HashSet<TileData> compatibleTiles = new HashSet<TileData>();
    private boolean hasConcaveCorners = true;
    
    @SuppressWarnings("unused")
    private AutoTileData() {
        super();
        // default constructor needed for serialization
    }
    
    public AutoTileData(
        final TileSetData tileset, 
        final Rect... src
    ) {
        super(tileset, src);
    }

    public AutoTileData setCompatibleWith(final TileData tile) {
        compatibleTiles.add(tile);
        return this;
    }
    
    public boolean isCompatibleWith(final TileData tile) {
        return 
            tile == this ||
            compatibleTiles.contains(tile);
    }
    
    @Override
    public AutoTileData setFrameDelay(final int delay) {
        super.setFrameDelay(delay);
        return this;
    }

    @Override
    public AutoTileData setAnimationSequence(final int... frames) {
        super.setAnimationSequence(frames);
        return this;
    }
    
    @Override
    public AutoTileData setPassable(final boolean passable) {
        super.setPassable(passable);
        return this;
    }
    
    public AutoTileData setConcaveCorners(final boolean hasConcaveCorners) {
        this.hasConcaveCorners = hasConcaveCorners;
        return this;
    }

    public boolean hasConcaveCorners() {
        return hasConcaveCorners;
    }
    
    @Override
    public Drawable getPreview() {
        if (preview == null) {
            preview = 
                new Drawable() {
                    @Override
                    public void draw(final Canvas canvas) {
                        canvas.drawBitmap(bitmap(), new Rect(src[0].left, src[0].top, src[0].left + 32, src[0].top + 32), getBounds(), null);
                    }
    
                    @Override
                    public int getOpacity() {
                        return PixelFormat.OPAQUE;
                    }
    
                    @Override
                    public void setAlpha(final int alpha) {
                        // do nothing
                    }
    
                    @Override
                    public void setColorFilter(final ColorFilter cf) {
                        // do nothing
                    }
                };
        }
        
        return preview;
    }
}
