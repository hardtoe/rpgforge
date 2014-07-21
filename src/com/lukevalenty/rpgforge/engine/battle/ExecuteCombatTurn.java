package com.lukevalenty.rpgforge.engine.battle;

import com.lukevalenty.rpgforge.engine.GameMessage;
import com.lukevalenty.rpgforge.engine.GameObject;

public class ExecuteCombatTurn extends GameMessage {
    public ExecuteCombatTurn(final GameObject sender) {
        super(sender);
    }
}
