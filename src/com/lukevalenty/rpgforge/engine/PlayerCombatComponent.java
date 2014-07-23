package com.lukevalenty.rpgforge.engine;

import com.lukevalenty.rpgforge.data.CharacterData;
import com.lukevalenty.rpgforge.engine.battle.ExecuteCombatTurn;

public class PlayerCombatComponent extends GameObjectComponent {
    private CharacterData charData;
    
    private ExecuteCombatTurn executeCombatTurn;
    private transient BooleanRef inCombat;

    private PlayerCombatComponent() {
        // for serialization
    }
    
    public PlayerCombatComponent(
        final CharacterData charData
    ) {
        this.charData = charData;
    }
    
    @Override
    public void init(
        final GameObject gameObject, 
        final GlobalGameState globalState
    ) {
        this.inCombat = gameObject.getBooleanRef("inCombat");
    }
    
    @Override
    public void update(
        final FrameState frameState, 
        final GlobalGameState globalState
    ) {


    }

    @Override
    public void onMessage(
        final GameMessage m
    ) {
        if (m instanceof ExecuteCombatTurn) {
            this.executeCombatTurn = (ExecuteCombatTurn) m;
        }
    }
}
