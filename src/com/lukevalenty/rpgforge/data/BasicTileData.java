package com.lukevalenty.rpgforge.data;

import android.graphics.Rect;

public class BasicTileData extends TileData {
    @SuppressWarnings("unused")
    private BasicTileData() {
        super();
        // default constructor needed for serialization
    }
    
    public BasicTileData(
        final TileSetData tileset, 
        final Rect... src
    ) {
        super(tileset, src);
    }
}
