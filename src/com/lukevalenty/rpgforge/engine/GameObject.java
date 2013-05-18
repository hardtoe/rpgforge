package com.lukevalenty.rpgforge.engine;

import java.util.ArrayList;

public class GameObject {
    final ArrayList<GameObjectComponent> components;
    
    public GameObject() {
        this.components = 
            new ArrayList<GameObjectComponent>();
    }
    
    public final void addComponent(
        final GameObjectComponent component
    ) {
        this.components.add(component);
    }
    
    public final void update(
        final FrameState frameState
    ) {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).update(frameState, this);
        }
    }
}
