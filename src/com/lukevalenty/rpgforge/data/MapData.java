package com.lukevalenty.rpgforge.data;

import java.util.ArrayList;
import java.util.Collection;

import com.lukevalenty.rpgforge.engine.GameObject;

import android.util.SparseArray;

public class MapData {
    private int width;
    private int height;
    
    private TileData[] tiles;
    private SparseArray<TileData> sparseTiles = new SparseArray<TileData>();
    private SparseArray<EventData> events = new SparseArray<EventData>();
    
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
        final int y,
        final int layer
    ) {
        if (layer == 0) {
            return getTile(x, y);
        } else {
            return getSparseTile(x, y);
        }
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
    
    public boolean setTile(
        final int x,
        final int y,
        final TileData tile
    ) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return false;
            
        } else {
            final int tileIndex = x + (y * width);
            
            if (tile.getLayer() == 0) {
                tiles[tileIndex] = tile;
                sparseTiles.delete(tileIndex);
                
            } else {
                sparseTiles.put(tileIndex, tile);
            }
            
            events.delete(tileIndex);
            
            return true;
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
                
                t = getSparseTile(x, y);
                
                if (t != null) {
                    resizedMapData.setTile(x, y, t);
                }
                
                EventData e = 
                    getEvent(x, y);
                
                if (e != null) {
                    resizedMapData.setEvent(x, y, e);
                }
            }
        }
        
        this.sparseTiles = resizedMapData.sparseTiles;
        this.events = resizedMapData.events;
        this.tiles = resizedMapData.tiles;
        this.width = newWidth;
        this.height = newHeight;
    }

    public EventData getEvent(
        final int x, 
        final int y
    ) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return null;
            
        } else {
            final Object event = 
                events.get(x + (y * width));
            
            if (event instanceof EventData) {
                return (EventData) event;
                
            } else {
                return null;
            }
        }
    }

    public void setEvent(
        final int x, 
        final int y,
        final EventData event
    ) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            // out of bounds, do nothing
        } else {
            events.put(x + (y * width), event);
        }
    }
    
    public ArrayList<GameObject> getGameObjects() {
        final ArrayList<GameObject> gameObjects =
            new ArrayList<GameObject>();
        
        for (int i = 0; i < events.size(); i++) {
            final Object object = 
                events.valueAt(i);
            
            // FIXME: these game object parameters need to be cleaned up...
            if (object instanceof DoorEventData) {
                final GameObject gameObject =
                    ((DoorEventData) object).getGameObject();
                
                gameObjects.add(gameObject);
                
                gameObject.getNumberRef("tileX").value = events.keyAt(i) % width;
                gameObject.getNumberRef("tileY").value = events.keyAt(i) / width;
            }
            
            if (object instanceof NpcEventData) {
                final GameObject gameObject =
                    ((NpcEventData) object).getGameObject();
                
                gameObjects.add(gameObject);
            }
        }
        
        return gameObjects;
    }
}
