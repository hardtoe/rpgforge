package com.lukevalenty.rpgforge.data;

import android.graphics.drawable.Drawable;

import com.lukevalenty.rpgforge.engine.CharacterRenderComponent;
import com.lukevalenty.rpgforge.engine.CollisionComponent;
import com.lukevalenty.rpgforge.engine.Direction;
import com.lukevalenty.rpgforge.engine.GameObject;
import com.lukevalenty.rpgforge.engine.LuaGameObjectComponent;
import com.lukevalenty.rpgforge.engine.MovementComponent;
import com.lukevalenty.rpgforge.engine.RandomWalkComponent;

public class EnemyEventData extends EventData {
    private GameObject eventGameObject;
    private int initialX;
    private int initialY;
    private boolean stationary;
    private Direction initialDirection;

    private EnemyCharacterData enemyCharacterData;

    private EnemyEventData() {
        // do nothing
    }
    
    public EnemyEventData(
        final EnemyCharacterData enemyCharacterData
    ) {
        this.enemyCharacterData = enemyCharacterData;
        
        eventGameObject = 
            new GameObject();      
    
        eventGameObject.addComponent(new RandomWalkComponent());
        eventGameObject.addComponent(new CollisionComponent());
        eventGameObject.addComponent(new MovementComponent());
        eventGameObject.addComponent(new CharacterRenderComponent(enemyCharacterData.getCharacterData()));
        eventGameObject.addComponent(new LuaGameObjectComponent("/com/lukevalenty/rpgforge/engine/EnemyCombatComponent.lua", enemyCharacterData));
    }
    
    @Override
    protected Drawable createPreview() {
        return enemyCharacterData.getPreview();
    }

    @Override
    public GameObject getGameObject() {
        eventGameObject.getNumberRef("x").value = initialX;
        eventGameObject.getNumberRef("y").value = initialY;
        eventGameObject.getBooleanRef("stationary").value = stationary;
        eventGameObject.getObjectRef("dir").value = initialDirection;
        return eventGameObject;
    }

    @Override
    public EventData create() {
        return new EnemyEventData(enemyCharacterData);
    }

    public void setInitialPosition(final int x, final int y) {
        initialX = x;
        initialY = y;
    }
    
    public CharacterData getCharacterData() {
        return enemyCharacterData.getCharacterData();
    }

    public void setStationary(final boolean stationary) {
        this.stationary = stationary;
    }

    public void setDirection(final Direction initialDirection) {
        this.initialDirection = initialDirection;
    }
}
