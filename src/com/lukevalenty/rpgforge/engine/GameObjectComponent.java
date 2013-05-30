package com.lukevalenty.rpgforge.engine;

public abstract class GameObjectComponent {
    public abstract void update(
        final FrameState frameState, 
        final GlobalGameState globalState);
    
    // FIXME: turn this into a message based system
    public void activate(
        final GameObject sender
    ) {
        // do nothing by default
    }

    // FIXME: turn this into a message based system
    public void walkOver(
        final GameObject sender
    ) {
        // do nothing by default
    }
}
