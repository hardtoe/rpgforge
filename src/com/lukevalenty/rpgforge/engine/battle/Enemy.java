package com.lukevalenty.rpgforge.engine.battle;

import com.lukevalenty.rpgforge.data.BattleCharacterData;
import com.lukevalenty.rpgforge.data.EnemyCharacterData;
import com.lukevalenty.rpgforge.engine.GameObject;

class Enemy extends CombatParticipant {
    public EnemyCharacterData characterData;
    public GameObject gameObject;
    
    public Enemy() {
        // do nothing
    }
    
    public Enemy(
        final EnemyCharacterData characterData, 
        final GameObject gameObject
    ) {
        this.characterData = characterData;
        this.gameObject = gameObject;
    }

    @Override
    public BattleCharacterData getBattleCharacterData() {
        return characterData;
    }

    @Override
    public GameObject getGameObject() {
        return gameObject;
    }
}