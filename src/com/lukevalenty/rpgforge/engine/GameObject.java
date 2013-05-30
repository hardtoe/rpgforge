package com.lukevalenty.rpgforge.engine;

import java.util.ArrayList;
import java.util.HashMap;

public class GameObject {
    private final ArrayList<GameObjectComponent> components;
    private final HashMap<String, Object> values;
    
    public GameObject() {
        this(new HashMap<String, Object>());
    }
    
    public GameObject(
        final HashMap<String, Object> values
    ) {
        this.components = 
            new ArrayList<GameObjectComponent>();
        
        this.values = 
            values;
    }
    
    public final void addComponent(
        final GameObjectComponent component
    ) {
        this.components.add(component);
    }
    
    public final void update(
        final FrameState frameState,
        final GlobalGameState globalState
    ) {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).update(frameState, globalState);
        }
    }
    
    /**
     * FIXME: make this into a message passing system
     * @param sender
     */
    public final void activate(final GameObject sender) {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).activate(sender);
        }
    }
    
    /**
     * FIXME: make this into a message passing system
     * @param sender
     */
    public final void walkOver(final GameObject sender) {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).walkOver(sender);
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

    public BooleanRef getBooleanRef(
        final String name
    ) {
        BooleanRef ref = 
            (BooleanRef) values.get(name);
            
        if (ref == null) {
            ref = new BooleanRef();
            values.put(name, ref);
        }
        
        return ref;
    }
}
