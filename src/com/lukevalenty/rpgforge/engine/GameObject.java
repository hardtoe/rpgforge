package com.lukevalenty.rpgforge.engine;

import java.util.ArrayList;
import java.util.HashMap;

import se.krka.kahlua.integration.annotations.LuaMethod;

public class GameObject {
    private final ArrayList<GameObjectComponent> components;
    
    // FIXME: may want to change this to an enumerated object hierarchy of some sort rather than string names
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
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " : " + components + " : " + values;
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
    

    public final void onMessage(final GameMessage msg) {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).onMessage(msg);
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

    public void init(final GlobalGameState globalState) {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).init(this, globalState);
        }
    }
    
    @LuaMethod(global = true)
    public Boolean getLocalFlag(final String name) {
        return getBooleanRef(name).value;
    }
    
    @LuaMethod(global = true)
    public void setLocalFlag(final String name, final boolean value) {
        getBooleanRef(name).value = value;
    }
    
    @LuaMethod(global = true)
    public void setLocalObject(final String name, final Object value) {
        getObjectRef(name).value = value;
    }

    public void reset() {
        values.clear();
    }
    
}
