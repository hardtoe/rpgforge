package com.lukevalenty.rpgforge.engine;

public class WalkOverMessage extends GameMessage {
    private WalkOverMessage() {
        super(null);
        // used only for serialization
    }
    
    public WalkOverMessage(final GameObject sender) {
        super(sender);
    }
}
