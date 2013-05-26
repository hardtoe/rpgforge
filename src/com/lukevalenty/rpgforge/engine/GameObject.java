package com.lukevalenty.rpgforge.engine;

import java.util.ArrayList;
import java.util.HashMap;

public class GameObject {
    final ArrayList<GameObjectComponent> components;
    final HashMap<String, Object> values;
    
    public GameObject() {
        this.components = 
            new ArrayList<GameObjectComponent>();
        
        this.values = 
            new HashMap<String, Object>();
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
            components.get(i).update(frameState);
        }
    }
    
    public NumberRef getNumberRef(
        final String name
    ) {
        NumberRef ref = 
            (NumberRef) values.get(name);
            
        if (ref == null) {
            ref = new NumberRef();
            values.put(name, ref);
        }
        
        return ref;
    }
    
    public <T> ObjectRef<T> getObjectRef(
        final String name
    ) {
        ObjectRef<T> ref = 
            (ObjectRef<T>) values.get(name);
            
        if (ref == null) {
            ref = new ObjectRef<T>();
            values.put(name, ref);
        }
        
        return ref;
    }
}
