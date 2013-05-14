package com.lukevalenty.rpgforge.data;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.TreeSet;

import android.graphics.Point;
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

    /**
     * Algorithm from: http://en.wikipedia.org/wiki/Flood_fill
     * @param replacementTile
     * @param x
     * @param y
     */
    /*
    public void fill(
        final TileData replacementTile, 
        final int x, 
        final int y
    ) {
        if (replacementTile.getLayer() == 0) {
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
                        addPoint(q, n);
                        q.addLast(new Point(n.x - 1, n.y));
                        q.addLast(new Point(n.x, n.y + 1));
                        q.addLast(new Point(n.x, n.y - 1));
                    }
                }
                
                q = null;
            }
        }
    }
    */

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
        if (replacementTile.getLayer() == 0) {
            final TileData targetTile = 
                getTile(x, y);
            
            if (targetTile != null && !replacementTile.equals(targetTile)) { 
                LinkedHashSet<Point> q = 
                    new LinkedHashSet<Point>();
                
                q.add(new Point(x, y));
                
                while (!q.isEmpty()) {
                    final Iterator<Point> i = q.iterator();
                    
                    final Point n = 
                        i.next();

                    i.remove();
                    
                    final TileData nTile = 
                        getTile(n.x, n.y);
                    
                    if (nTile != null && nTile == targetTile) {
                        setTile(n.x, n.y, replacementTile);
                        addPoint(targetTile, q, new Point(n.x + 1, n.y));
                        addPoint(targetTile, q, new Point(n.x - 1, n.y));
                        addPoint(targetTile, q, new Point(n.x, n.y + 1));
                        addPoint(targetTile, q, new Point(n.x, n.y - 1));
                    }
                }
                
                q = null;
            }
        }
    }

    private void addPoint(TileData targetTile, LinkedHashSet<Point> q, final Point p) {
        final TileData tile = getTile(p.x, p.y);
        
        if (tile != null && tile.equals(targetTile)) {
            q.add(p);
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
