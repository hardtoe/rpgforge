package com.lukevalenty.rpgforge.engine;

public class ActivateMessage extends GameMessage {
    private ActivateMessage() {
        super(null);
        // used only for serialization
    }
    
    public ActivateMessage(final GameObject sender) {
        super(sender);
    }
}
