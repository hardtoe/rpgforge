package com.lukevalenty.rpgforge.data;

import java.util.ArrayList;
import java.util.List;

public class TileSetData extends BitmapData {
    private ArrayList<TileData> tiles = new ArrayList<TileData>();
    
    protected TileSetData() {
        // default constructor needed for serialization
        super();
    }
    
    public TileSetData(
        final String bitmapFilePath
    ) {
        super(bitmapFilePath);
    }
    
    public void addTile(final TileData tile) {
        this.tiles.add(tile);
    }
    
    public List<TileData> getTiles() {
        return tiles;
    }
}
