package com.lukevalenty.rpgforge.engine.battle;

import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;

import com.lukevalenty.rpgforge.data.EnemyCharacterData;
import com.lukevalenty.rpgforge.data.PlayerCharacterData;
import com.lukevalenty.rpgforge.engine.FrameState;
import com.lukevalenty.rpgforge.engine.GameObject;
import com.lukevalenty.rpgforge.engine.GameObjectComponent;
import com.lukevalenty.rpgforge.engine.GamePhase;
import com.lukevalenty.rpgforge.engine.GlobalGameState;
import com.lukevalenty.rpgforge.engine.NumberRef;
import com.lukevalenty.rpgforge.engine.ObjectRef;

public class BattleZoneComponent extends GameObjectComponent {  
    private transient GameObject gameObject;
    
    private transient NumberRef x1;
    private transient NumberRef y1;
    private transient NumberRef x2;
    private transient NumberRef y2;
    
    private transient ObjectRef<ArrayList<Enemy>> enemies;
    private transient ObjectRef<ArrayList<Player>> players;
    
    private transient NumberRef xPlayer;
    private transient NumberRef yPlayer;
    
    private transient ObjectRef<ArrayList<CombatParticipant>> combatParticipants;

    private transient ExecuteCombatTurn EXECUTE_COMBAT_TURN;
    
    public BattleZoneComponent() {
        // do nothing
    }

    @Override
    public void init(
        final GameObject gameObject, 
        final GlobalGameState globalState
    ) {
        this.x1 =
            gameObject.getNumberRef("x1");
        
        this.y1 =
            gameObject.getNumberRef("y1");
        
        this.x2 =
            gameObject.getNumberRef("x2");
        
        this.y2 =
            gameObject.getNumberRef("y2");
        
        this.enemies =
            gameObject.getObjectRef("enemies");
        
        this.players =
            gameObject.getObjectRef("players");

        this.combatParticipants =
            gameObject.getObjectRef("combatParticipants");
        
        this.xPlayer =
            globalState.getPlayer().getNumberRef("x");
        
        this.yPlayer =
            globalState.getPlayer().getNumberRef("y");
        
        if (enemies.value == null) {
            enemies.value = new ArrayList<Enemy>();
        }
        
        if (players.value == null) {
            players.value = new ArrayList<Player>();
        }
        
        if (combatParticipants.value == null) {
            combatParticipants.value = new ArrayList<CombatParticipant>();
        }
        
        EXECUTE_COMBAT_TURN = new ExecuteCombatTurn(gameObject).setFinished(true);
    }
    
    
    private int combatParticipantIndex = 0;
    
    // FIXME: maybe this should be recoded more explicitly as a state machine
    @Override
    public void update(
        final FrameState frameState, 
        final GlobalGameState globalState
    ) {
        if (frameState.phase == GamePhase.UPDATE) {
            if (globalState.isBattle()) {
                if (EXECUTE_COMBAT_TURN.isFinished()) {
                    CombatParticipant currentCombatParticipant = getNextParticipant();
                    currentCombatParticipant.getGameObject().onMessage(EXECUTE_COMBAT_TURN.setFinished(false));

                    Log.i(getClass().getCanonicalName(), "STARTING TURN FOR " + currentCombatParticipant.getBattleCharacterData().getName());
                }
 
            } else {  
                if (
                    xPlayer.value > x1.value && xPlayer.value < x2.value &&
                    yPlayer.value > y1.value && yPlayer.value < y2.value
                ) {
                    // initiate battle
                    globalState.setBattle(true);
                    Log.i(getClass().getCanonicalName(), "BATTLE INITIATED");

                    gatherPlayerCharacters(globalState);
                    gatherEnemyCharacters(globalState);
                    
                    // FIXME: need to sort these guys based on agility or other appropriate trait
                    combatParticipantIndex = 0;
                }
            }
        }
    }

    private CombatParticipant getNextParticipant() {
        final CombatParticipant combatParticipant = 
            combatParticipants.value.get(combatParticipantIndex);
        
        combatParticipantIndex = 
            (combatParticipantIndex + 1) % combatParticipants.value.size();
        
        return 
            combatParticipant;
    }

    private void gatherEnemyCharacters(
        final GlobalGameState globalState
    ) {
        // FIXME: probably want a better algorithm for this. may need to use
        //        a data structure for game objects that is based on location within
        //        the map.
        for (final GameObject o : globalState.mapGameObjects) {
            if (
                o.getBooleanRef("isEnemy").value &&

                o.getNumberRef("x").value > x1.value && o.getNumberRef("x").value < x2.value &&
                o.getNumberRef("y").value > y1.value && o.getNumberRef("y").value < y2.value
            ) {
                Log.i(getClass().getCanonicalName(), "adding enemy to BattleZone");
                o.getBooleanRef("inCombat").value = true;
                o.getBooleanRef("walking").value = false;
                addEnemy((EnemyCharacterData) o.getObjectRef("enemyCharacterData").value, o);
            }
        }
    }

    private void gatherPlayerCharacters(
        final GlobalGameState globalState
    ) {
        Log.i(getClass().getCanonicalName(), "adding player to BattleZone");
        globalState.getPlayer().getBooleanRef("inCombat").value = true;
        globalState.getPlayer().getBooleanRef("walking").value = false;
        globalState.getPlayer().getNumberRef("dx").value = 0;
        globalState.getPlayer().getNumberRef("dy").value = 0;
        addPlayer(globalState.getPlayer().getPlayerCharacterData(), globalState.getPlayer());
    }    


    public void addEnemy(
        final EnemyCharacterData enemyCharacterData,
        final GameObject enemyGameObject
    ) {
        final Enemy enemy = new Enemy(enemyCharacterData, enemyGameObject);
        enemies.value.add(enemy);
        combatParticipants.value.add(enemy);
    }

    public void addPlayer(
        final PlayerCharacterData playerCharacterData,
        final GameObject playerGameObject
    ) {
        final Player player = new Player(playerCharacterData, playerGameObject);
        players.value.add(player);
        combatParticipants.value.add(player);
    }
}