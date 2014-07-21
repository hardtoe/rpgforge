package com.lukevalenty.rpgforge.engine.battle;

import com.lukevalenty.rpgforge.data.EnemyCharacterData;
import com.lukevalenty.rpgforge.engine.FrameState;
import com.lukevalenty.rpgforge.engine.GameObject;
import com.lukevalenty.rpgforge.engine.GameObjectComponent;
import com.lukevalenty.rpgforge.engine.GamePhase;
import com.lukevalenty.rpgforge.engine.GlobalGameState;
import com.lukevalenty.rpgforge.engine.NumberRef;

public class EnemyComponent extends GameObjectComponent {
    private EnemyCharacterData enemyCharacterData;
    
    private transient NumberRef playerX;
    private transient NumberRef playerY;
    private transient NumberRef enemyX;
    private transient NumberRef enemyY;
    private transient GameObject enemyGameObject;
    
    private EnemyComponent() {
        // used for serialization
    }
    
    public EnemyComponent(
        final EnemyCharacterData enemyCharacterData
    ) {
        this.enemyCharacterData = enemyCharacterData;
    }

    @Override
    public void init(
        final GameObject gameObject, 
        final GlobalGameState globalState
    ) {
        this.enemyGameObject =
            gameObject;
        
        this.enemyX =
            gameObject.getNumberRef("x");
        
        this.enemyY =
            gameObject.getNumberRef("y");
        
        this.playerX = 
            globalState.getPlayer().getNumberRef("x");
        
        this.playerY = 
            globalState.getPlayer().getNumberRef("y");
        
        gameObject.getBooleanRef("isEnemy").value = true;
        gameObject.getObjectRef("enemyCharacterData").value = enemyCharacterData;
    }
    
    @Override
    public void update(
        final FrameState frameState, 
        final GlobalGameState globalState
    ) {
        
    }
}
