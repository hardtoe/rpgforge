package com.lukevalenty.rpgforge.engine.battle;

import com.lukevalenty.rpgforge.data.EnemyCharacterData;
import com.lukevalenty.rpgforge.engine.GameObject;

class Enemy {
    public final EnemyCharacterData characterData;
    public final GameObject gameObject;
    
    public Enemy(
        final EnemyCharacterData characterData, 
        final GameObject gameObject
    ) {
        this.characterData = characterData;
        this.gameObject = gameObject;
    }
}