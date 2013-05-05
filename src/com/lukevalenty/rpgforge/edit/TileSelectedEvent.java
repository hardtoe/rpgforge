package com.lukevalenty.rpgforge.edit;

import com.lukevalenty.rpgforge.data.TileData;

public class TileSelectedEvent {
    private final TileData tile;
    
    public TileSelectedEvent(
        final TileData tile
    ) {
        this.tile = tile;
    }
    
    public TileData tile() {
        return tile;
    }
}
