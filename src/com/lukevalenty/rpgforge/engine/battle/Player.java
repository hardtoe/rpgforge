package com.lukevalenty.rpgforge.engine.battle;

import com.lukevalenty.rpgforge.data.BattleCharacterData;
import com.lukevalenty.rpgforge.data.PlayerCharacterData;
import com.lukevalenty.rpgforge.engine.GameObject;

class Player extends CombatParticipant {
    private PlayerCharacterData characterData;
    private GameObject gameObject;
    
    public Player() {
        // do nothing
    }
    
    public Player(
        final PlayerCharacterData characterData, 
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