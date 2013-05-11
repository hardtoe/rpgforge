package com.lukevalenty.rpgforge.data;

import java.util.ArrayList;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Shader.TileMode;

/**
 * Contains all data for an RPG.
 */
public class RpgDatabase {
    private ArrayList<MapData> maps;
    private ArrayList<TileSetData> tileSets;
    private TileData defaultTile;
    
    public RpgDatabase() {
        this.maps = new ArrayList<MapData>();
        this.tileSets = new ArrayList<TileSetData>();
    }
    
    public void setDefaultTile(final TileData newDefaultTile) {
        this.defaultTile = newDefaultTile;
    }
    
    public TileData getDefaultTile() {
        if (defaultTile == null) {
            return tileSets.get(4).getTiles().get(16);
            
        } else {
            return this.defaultTile;
        }
    }
    
    public LinkedList<MapData> getMaps() {
        final LinkedList<MapData> mapsCopy = new LinkedList<MapData>();
        mapsCopy.addAll(maps);
        return mapsCopy;
    }
    
    public LinkedList<TileSetData> getTileSets() {
        final LinkedList<TileSetData> tileSetsCopy = new LinkedList<TileSetData>();
        tileSetsCopy.addAll(tileSets);
        return tileSetsCopy; 
    }
    
    public void addMap(final MapData map) {
        maps.add(map);
    }
    
    public void removeMap(final MapData map) {
        maps.remove(map);
    }
    
    public void addTileSet(final TileSetData tileSet) {
        tileSets.add(tileSet);
    }
    
    public void removeTileSet(final TileSetData tileSet) {
        tileSets.remove(tileSet);
    }
    
    public ArrayList<TileData> getAllTiles() {
        final ArrayList<TileData> allTiles =
            new ArrayList<TileData>();
        
        for (final TileSetData tileSet : tileSets) {
            allTiles.addAll(tileSet.getTiles());
        }
        
        return allTiles;
    }

    public void load(final Context context) {
        for (final TileSetData tileSet : tileSets) {
            tileSet.load(context);
        }
    }
}
