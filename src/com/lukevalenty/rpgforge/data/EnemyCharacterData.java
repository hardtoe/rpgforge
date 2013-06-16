package com.lukevalenty.rpgforge.data;

public class EnemyCharacterData extends BattleCharacterData {
    @SuppressWarnings("unused")
    private EnemyCharacterData() {
        // default constructor needed for serialization
        super();
    }
    
    public EnemyCharacterData(
        final CharacterData charData
    ) {
        super(charData);
    }
}
