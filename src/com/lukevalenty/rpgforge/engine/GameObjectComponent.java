package com.lukevalenty.rpgforge.engine;

public abstract class GameObjectComponent {
    public abstract void update(
        final FrameState frameState, 
        final GlobalGameState globalState);
    
    public void activate(
        final GameObject sender
    ) {
        // do nothing by default
    }
}
