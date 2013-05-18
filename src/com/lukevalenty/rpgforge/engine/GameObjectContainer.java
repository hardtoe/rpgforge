package com.lukevalenty.rpgforge.engine;

import java.util.ArrayList;

public class GameObjectContainer {
    private ArrayList<GameObject> objects;
    private ArrayList<GameObjectContainer> containers;
    
    public GameObjectContainer() {
        this.objects = 
            new ArrayList<GameObject>();
        
        this.containers = 
            new ArrayList<GameObjectContainer>();
    }
    
    public void add(final GameObject object) {
        this.objects.add(object);
    }
    
    public void add(final GameObjectContainer container) {
        this.containers.add(container);
    }
    
    public void update(
        final FrameState frameState
    ) {
        for (int i = 0; i < objects.size(); i++) {
            objects.get(i).update(frameState);
        }
        
        for (int i = 0; i < containers.size(); i++) {
            containers.get(i).update(frameState);
        }
    }
}
