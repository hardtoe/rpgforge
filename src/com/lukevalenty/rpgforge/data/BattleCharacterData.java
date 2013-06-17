package com.lukevalenty.rpgforge.data;

import android.graphics.Bitmap;

public class BattleCharacterData {
    private CharacterData charData;
    private String name;
    
    @SuppressWarnings("unused")
    protected BattleCharacterData() {
        // default constructor needed for serialization
        super();
    }
    
    public BattleCharacterData(
        final CharacterData charData
    ) {
        this.charData = charData;
    }

    public CharacterData getCharacterData() {
        return this.charData;
    }

    public void setCharacterData(
        final CharacterData characterData
    ) {
        this.charData = characterData;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
