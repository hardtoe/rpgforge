package com.lukevalenty.rpgforge.data;

import android.util.SparseArray;

public class MapData {
    private int width;
    private int height;
    
    private TileData[] tiles;
    private SparseArray<TileData> sparseTiles = new SparseArray<TileData>();
    
    private String name;
    
    
    @SuppressWarnings("unused")
    private MapData() {
        // default constructor needed for serialization
    }
    
    public MapData(
        final int width,
        final int height
    ) {
        this.width = width;
        this.height = height;
        this.tiles = new TileData[width * height];
    }
    
    public MapData(
        final int width,
        final int height,
        final TileData... tiles
    ) {
        this.width = width;
        this.height = height;
        this.tiles = tiles;
    }
    
    public void setName(
        final String newName
    ) {
        this.name = newName;
    }
    
    public String getName() {
        if (name == null) {
            return "NONAME";
            
        } else {
            return name;
        }
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public TileData getTile(
        final int x,
        final int y
    ) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return null;
            
        } else {
            return tiles[x + (y * width)];
        }
    }
    
    public TileData getSparseTile(
        final int x,
        final int y
    ) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return null;
            
        } else {
            if (sparseTiles.get(x + (y * width)) instanceof TileData) {
                return sparseTiles.get(x + (y * width));
            } else {
                return null;
            }
        }
    }
    
    public void setTile(
        final int x,
        final int y,
        final TileData tile
    ) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            // out of bounds, do nothing
            
        } else {
            final int tileIndex = x + (y * width);
            
            if (tile.getLayer() == 0) {
                tiles[tileIndex] = tile;
                sparseTiles.delete(tileIndex);
                
            } else {
                sparseTiles.put(tileIndex, tile);
            }
        }
    }

    public void fill(final TileData tile) {
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = tile;
        }
    }


    public void resize(
        final int newWidth, 
        final int newHeight,
        final TileData fillTile
    ) {
        final MapData resizedMapData = 
            new MapData(newWidth, newHeight);
        
        for (int x = 0; x < newWidth; x++) {
            for (int y = 0; y < newHeight; y++) {
                TileData t = 
                    getTile(x, y);
                
                if (t == null) {
                    t = fillTile;
                }
                
                resizedMapData.setTile(x, y, t);
            }
        }
        
        this.tiles = resizedMapData.tiles;
        this.width = newWidth;
        this.height = newHeight;
    }
}
