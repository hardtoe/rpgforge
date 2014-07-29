package com.lukevalenty.rpgforge.engine;

import se.krka.kahlua.integration.annotations.LuaMethod;

public class GameMessage {
    private final GameObject sender;

    public GameMessage(final GameObject sender) {
        this.sender = sender;
    }
    
    public GameObject sender() {
        return sender;
    }
    
    @LuaMethod
    public String getName() {
        return getClass().getSimpleName();
    }
}
