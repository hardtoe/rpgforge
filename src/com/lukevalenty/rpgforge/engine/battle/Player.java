package com.lukevalenty.rpgforge.engine.battle;

import com.lukevalenty.rpgforge.data.PlayerCharacterData;
import com.lukevalenty.rpgforge.engine.GameObject;

class Player {
    public final PlayerCharacterData characterData;
    public final GameObject gameObject;
    
    public Player(
        final PlayerCharacterData characterData, 
        final GameObject gameObject
    ) {
        this.characterData = characterData;
        this.gameObject = gameObject;
    }
}