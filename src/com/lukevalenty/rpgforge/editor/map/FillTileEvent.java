package com.lukevalenty.rpgforge.editor.map;

import com.lukevalenty.rpgforge.data.TileData;

public class FillTileEvent {
    private final TileData tile;
    private final int x;
    private final int y;
    
    public FillTileEvent(
        final TileData tile,
        final int x,
        final int y
    ) {
        this.tile = tile;
        this.x = x;
        this.y = y;
    }

    public TileData tile() {
        return tile;
    }
    
    public int x() {
        return x;
    }
    
    public int y() {
        return y;
    }
}
