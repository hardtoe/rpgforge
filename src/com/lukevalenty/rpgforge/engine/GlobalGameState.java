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
    
    private transient FrameState frameState;
    
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

    public void setFrameState(final FrameState frameState) {
        this.frameState = frameState;
    }
    
    @LuaMethod(global = true)
    public void log(final String tag, final String msg) {
        Log.i(tag, msg);
    }
   
    @LuaMethod(global = true)
    public void displaySelectionWindow(
        final double x1,
        final double y1, 
        final double x2, 
        final double y2, 
        final double selection, 
        final String... options
    ) {
        frameState.drawBuffer.add(frameState.dialogPool.get().set((int) x1, (int) y1, (int) x2, (int) y2, options).setSelection((int) selection).setZ(100000));
    }
   
    @LuaMethod(global = true)
    public void displayDialogWindow(
        final double x1, 
        final double y1, 
        final double x2, 
        final double y2, 
        final String... text
    ) {
        frameState.drawBuffer.add(frameState.dialogPool.get().set((int) x1, (int) y1, (int) x2, (int) y2, text).setDialog().setZ(100000));
    }

    @LuaMethod(global = true)
    public Boolean isUpPressed() {
        return gameInput.up();
    }

    @LuaMethod(global = true)
    public Boolean isDownPressed() {
        return gameInput.down();
    }

    @LuaMethod(global = true)
    public Boolean isLeftPressed() {
        return gameInput.left();
    }

    @LuaMethod(global = true)
    public Boolean isRightPressed() {
        return gameInput.right();
    }

    @LuaMethod(global = true)
    public Boolean isActionPressed() {
        return gameInput.action();
    }

    @LuaMethod(global = true)
    public Boolean isBackPressed() {
        return gameInput.back();
    }
    
}
