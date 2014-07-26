package com.lukevalenty.rpgforge.engine;

import java.util.ArrayList;

import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.integration.LuaCaller;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.integration.expose.LuaJavaClassExposer;
import se.krka.kahlua.j2se.J2SEPlatform;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.LuaClosure;

import android.util.Log;

import com.lukevalenty.rpgforge.data.MapData;
import com.lukevalenty.rpgforge.engine.battle.BattleZoneEventData;
import com.lukevalenty.rpgforge.engine.input.GameInput;

public class GlobalGameState {
    private MapData map;
    private GameInput gameInput;
    private int xFocus;
    private int yFocus;
    
    // FIXME: need to encapsulate this field
    public GameObjectContainer gameTree;
    
    private GameObjectContainer mapGameObjectContainer;
    
    // FIXME: probably want a better data structure for this
    // FIXME: need to encapsulate this field
    public ArrayList<GameObject> mapGameObjects;
    
    private PlayerCharacter player;
    
    // true when player is in battle
    private boolean battle;
    
    public GlobalGameState() {
        gameTree = 
            new GameObjectContainer(); 
        
        mapGameObjectContainer =
            new GameObjectContainer();
        
        gameTree.add(mapGameObjectContainer);
    }

    public void setMap(final MapData newMap) {
        mapGameObjectContainer.clear();
        
        this.map = newMap;

        mapGameObjects =
            newMap.getGameObjects();
        
        for (int i = 0; i < mapGameObjects.size(); i++) {
            mapGameObjectContainer.add(mapGameObjects.get(i));
        }
        
        gameTree.init(this);
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
     * @param msg
     * @param x
     * @param y
     */
    public void sendMessage(
        final GameMessage msg,
        final int x, 
        final int y
    ) {
        for (GameObject o : mapGameObjects) {
            int tileX = (int) o.getNumberRef("tileX").value;
            int tileY = (int) o.getNumberRef("tileY").value;
            
            if (tileX == x && tileY == y) {
                o.onMessage(msg);
            }
        }
    }

    public void setPlayer(final PlayerCharacter player) {
        this.player = player;
    }
    
    public PlayerCharacter getPlayer() {
        return this.player;
    }
    
    public boolean isBattle() {
        return battle;
    }
    
    public void setBattle(final boolean battle) {
        this.battle = battle;
    }
    
    @LuaMethod(global = true)
    public void log(final String msg) {
        Log.i(getClass().getCanonicalName(), msg);
    }
    

/*    
    @LuaMethod(global = true)
    public void displaySelectionDialog(final String msg) {
        frameState.drawBuffer.add(frameState.dialogPool.get().set(12, 4, 16, 8, "Attack", "Move").setSelection(0).setZ(100000));
    }
    */
}
