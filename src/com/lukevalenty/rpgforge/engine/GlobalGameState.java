package com.lukevalenty.rpgforge.engine;

import com.lukevalenty.rpgforge.data.MapData;
import com.lukevalenty.rpgforge.engine.input.GameInput;

public class GlobalGameState {
    private MapData map;
    private GameInput gameInput;

    public void setMap(final MapData map) {
        this.map = map;
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
}
