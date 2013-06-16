package com.lukevalenty.rpgforge.data;

import android.graphics.Bitmap;

public class PlayerCharacterData extends BattleCharacterData {
    @SuppressWarnings("unused")
    private PlayerCharacterData() {
        // default constructor needed for serialization
        super();
    }
    
    public PlayerCharacterData(
        final CharacterData charData
    ) {
        super(charData);
    }
}
