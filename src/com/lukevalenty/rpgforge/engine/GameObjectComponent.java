package com.lukevalenty.rpgforge.engine;

public abstract class GameObjectComponent {
    public abstract void update(
        final FrameState frameState, 
        final GameObject gameObject);
}
