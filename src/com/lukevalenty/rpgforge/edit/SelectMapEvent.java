package com.lukevalenty.rpgforge.edit;

import com.lukevalenty.rpgforge.data.MapData;

public class SelectMapEvent {
    final int mapIndex;
    final MapData map;
    boolean mapListChanged;
    boolean newMapAdded;
    boolean mapDeleted;
    
    public SelectMapEvent(
        final int mapIndex, 
        final MapData map
    ) {
        this.mapIndex = mapIndex;
        this.map = map;
    }
    
    public SelectMapEvent setNewMapAdded() {
        mapListChanged = true;
        newMapAdded = true;
        return this;
    }
    
    public SelectMapEvent setMapDeleted() {
        mapListChanged = true;
        mapDeleted = true;
        return this;
    }
    
    public SelectMapEvent setMapListChanged() {
        mapListChanged = true;
        return this;
    }
    
    public int mapIndex() {
        return mapIndex;
    }
    
    public MapData map() {
        return map;
    }
    
    public boolean mapListChanged() {
        return mapListChanged;
    }
    
    public boolean newMapAdded() {
        return newMapAdded;
    }
    
    public boolean mapDeleted() {
        return mapDeleted;
    }
}
