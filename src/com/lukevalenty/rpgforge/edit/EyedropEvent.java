package com.lukevalenty.rpgforge.edit;

import java.util.List;

import com.lukevalenty.rpgforge.data.TileData;

public class EyedropEvent {
    private final int x;
    private final int y;
    
    private TileData tile;
    private List<TileData> sparseTiles;
    
    public EyedropEvent(
        final int x,
        final int y
    ) {
        this.x = x;
        this.y = y;
    }
    
    public int x() {
        return x;
    }
    
    public int y() {
        return y;
    }
    
    public void setTile(final TileData tile) {
        this.tile = tile;
    }
    
    public TileData tile() {
        return tile;
    }

    public void setSparseTiles(final List<TileData> sparseTiles) {
        this.sparseTiles = sparseTiles;
    }
    
    public List<TileData> sparseTiles() {
        return sparseTiles;
    }
}
