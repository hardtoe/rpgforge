package com.lukevalenty.rpgforge.engine.battle;

import com.lukevalenty.rpgforge.engine.GameMessage;
import com.lukevalenty.rpgforge.engine.GameObject;

public class CombatTurnFinished extends GameMessage {
    public CombatTurnFinished(final GameObject sender) {
        super(sender);
    }
}
