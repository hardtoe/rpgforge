package com.lukevalenty.rpgforge.engine.battle;

import java.util.ArrayList;

import com.lukevalenty.rpgforge.data.EnemyCharacterData;
import com.lukevalenty.rpgforge.data.PlayerCharacterData;
import com.lukevalenty.rpgforge.engine.FrameState;
import com.lukevalenty.rpgforge.engine.GameObject;
import com.lukevalenty.rpgforge.engine.GameObjectComponent;
import com.lukevalenty.rpgforge.engine.GlobalGameState;
import com.lukevalenty.rpgforge.engine.NumberRef;

public class BattleZoneComponent extends GameObjectComponent {   
    private ArrayList<Enemy> enemies;
    private ArrayList<Player> players;
    private boolean active;
    
    private transient GameObject gameObject;
    private transient NumberRef x1;
    private transient NumberRef y1;
    private transient NumberRef x2;
    private transient NumberRef y2;

    public BattleZoneComponent() {
        // do nothing
    }
    
    public BattleZoneComponent(
        final ArrayList<Enemy> enemies
    ) {
        this.enemies = enemies;
        this.players = new ArrayList<Player>();    
    }

    @Override
    public void init(
        final GameObject gameObject, 
        final GlobalGameState globalState
    ) {
        this.gameObject =
            gameObject;
        
        this.x1 =
            gameObject.getNumberRef("x1");
        
        this.y1 =
            gameObject.getNumberRef("y1");
        
        this.x2 =
            gameObject.getNumberRef("x2");
        
        this.y2 =
            gameObject.getNumberRef("y2");
    }
    
    @Override
    public void update(
        final FrameState frameState, 
        final GlobalGameState globalState
    ) {
        // TODO Auto-generated method stub
        
    }    

    public void addEnemy(
        final EnemyCharacterData enemyCharacterData,
        final GameObject enemyGameObject
    ) {
        enemies.add(new Enemy(enemyCharacterData, enemyGameObject));
    }

    public void addPlayer(
        final PlayerCharacterData playerCharacterData,
        final GameObject playerGameObject
    ) {
        players.add(new Player(playerCharacterData, playerGameObject));
    }

    public void startBattle() {
        this.active = true;
    }
    
    public void stopBattle() {
        this.active = false;
    }
    
    public boolean active() {
        return active;
    }
}