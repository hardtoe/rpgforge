package com.lukevalenty.rpgforge.engine.battle;

import se.krka.kahlua.integration.annotations.LuaMethod;

import com.lukevalenty.rpgforge.engine.GameMessage;
import com.lukevalenty.rpgforge.engine.GameObject;

public class ExecuteCombatTurn extends GameMessage {
    private boolean isFinished;

    private ExecuteCombatTurn() {
        super(null);
        // only used for serialization
    }
    
    public ExecuteCombatTurn(final GameObject sender) {
        super(sender);
        this.isFinished = false;
    }
    
    @LuaMethod
    public Boolean isFinished() {
        return isFinished;
    }
    
    @LuaMethod
    public ExecuteCombatTurn setFinished(final boolean isFinished) {
        this.isFinished = isFinished;
        return this;
    }
}
