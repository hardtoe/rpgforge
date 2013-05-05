package com.lukevalenty.rpgforge.data;

import java.util.HashSet;

import android.graphics.Rect;

public class AutoTileData extends TileData {
    private HashSet<TileData> compatibleTiles = new HashSet<TileData>();

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

    @Override
    public Rect getPreview() {
        return new Rect(src[0].left, src[0].top, src[0].left + 32, src[0].top + 32);
    }
}
