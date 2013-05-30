package com.lukevalenty.rpgforge.engine;

import java.util.ArrayList;

import android.util.Log;

import com.lukevalenty.rpgforge.data.MapData;
import com.lukevalenty.rpgforge.engine.input.GameInput;

public class GlobalGameState {
    private MapData map;
    private GameInput gameInput;
    private int xFocus;
    private int yFocus;
    public GameObjectContainer gameTree;
    
    // FIXME: probably want a better data structure for this
    // FIXME: need to encapsulate this field
    public ArrayList<GameObject> mapGameObjects;

    public void setMap(final MapData newMap) {
        if (newMap != this.map && this.map != null) {
            mapGameObjects =
                this.map.getGameObjects();
            
            for (int i = 0; i < mapGameObjects.size(); i++) {
                gameTree.remove(mapGameObjects.get(i));
            }
        }
        
        this.map = newMap;

        mapGameObjects =
            newMap.getGameObjects();
        
        for (int i = 0; i < mapGameObjects.size(); i++) {
            gameTree.add(mapGameObjects.get(i));
        }
    }

    public MapData getMap() {
        return map;
    }
    
    public GameInput getGameInput() {
        return gameInput;
    }

    public void setGameInput(final GameInput gameInput) {
        this.gameInput = gameInput;
    }

    public void setFocus(int x, int y) {
        this.xFocus = x;
        this.yFocus = y;
    }
    
    public int getXFocus() {
        return xFocus;
    }
    
    public int getYFocus() {
        return yFocus;
    }

    /**
     * FIXME: make this into a message passing system
     * @param sender
     * @param x
     * @param y
     */
    public void activate(
        final GameObject sender,
        final int x, 
        final int y
    ) {
        for (GameObject o : mapGameObjects) {
            int tileX = (int) o.getNumberRef("tileX").value;
            int tileY = (int) o.getNumberRef("tileY").value;
            
            if (tileX == x && tileY == y) {
                o.activate(sender);
            }
        }
    }

    /**
     * FIXME: make this into a message passing system
     */
    public void walkOver(
        final GameObject sender, 
        final int x, 
        final int y
    ) {
        for (GameObject o : mapGameObjects) {
            int tileX = (int) o.getNumberRef("tileX").value;
            int tileY = (int) o.getNumberRef("tileY").value;
            
            if (tileX == x && tileY == y) {
                o.walkOver(sender);
            }
        }
    }
}
