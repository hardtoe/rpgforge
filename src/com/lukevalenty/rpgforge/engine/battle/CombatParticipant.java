package com.lukevalenty.rpgforge.engine.battle;

import com.lukevalenty.rpgforge.data.BattleCharacterData;
import com.lukevalenty.rpgforge.engine.GameObject;

public abstract class CombatParticipant {
    public abstract BattleCharacterData getBattleCharacterData();
    
    public abstract GameObject getGameObject();
}
