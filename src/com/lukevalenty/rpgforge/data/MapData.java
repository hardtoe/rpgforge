package com.lukevalenty.rpgforge.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Point;
import android.util.SparseArray;

public class MapData {
    private int width;
    private int height;
    
    private TileData[] tiles;
    
    private String name;
    
    private SparseArray<List<TileData>> sparseTiles = 
        new SparseArray<List<TileData>>();
    
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
    
    public List<TileData> getSparseTiles(
        final int x,
        final int y
    ) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return null;
            
        } else {
            return sparseTiles.get(x + (y * width));
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
                sparseTiles.put(tileIndex, null);
                
            } else {
                List<TileData> sparseTileList = 
                    sparseTiles.get(tileIndex);
                
                if (sparseTileList == null) {
                    sparseTileList = new ArrayList<TileData>();
                    sparseTiles.put(tileIndex, sparseTileList);
                }
                
                sparseTileList.add(tile);
            }
        }
    }

    public void fill(final TileData tile) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                setTile(x, y, tile);
            }
        } 
    }

    /**
     * Algorithm from: http://en.wikipedia.org/wiki/Flood_fill
     * @param replacementTile
     * @param x
     * @param y
     */
    public void fill(
        final TileData replacementTile, 
        final int x, 
        final int y
    ) {
        final TileData targetTile = 
            getTile(x, y);
        
        if (targetTile != null && !replacementTile.equals(targetTile)) { 
            LinkedList<Point> q = 
                new LinkedList<Point>();
            
            q.addLast(new Point(x, y));
            
            while (!q.isEmpty()) {
                final Point n = 
                    q.removeLast();
                
                final TileData nTile = 
                    getTile(n.x, n.y);
                
                if (nTile != null && nTile == targetTile) {
                    setTile(n.x, n.y, replacementTile);
                    q.addLast(new Point(n.x + 1, n.y));
                    q.addLast(new Point(n.x - 1, n.y));
                    q.addLast(new Point(n.x, n.y + 1));
                    q.addLast(new Point(n.x, n.y - 1));
                }
            }
            
            q = null;
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
