package com.lukevalenty.rpgforge.engine;

public class GameMessage {
    private final GameObject sender;

    public GameMessage(final GameObject sender) {
        this.sender = sender;
    }
    
    public GameObject sender() {
        return sender;
    }
}
