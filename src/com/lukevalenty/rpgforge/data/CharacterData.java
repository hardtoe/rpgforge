package com.lukevalenty.rpgforge.data;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class CharacterData {
    private CharacterSetData characterSet;
    private Rect src;

    @SuppressWarnings("unused")
    private CharacterData() {
        // default constructor needed for serialization
        super();
    }
    
    public CharacterData(
        final CharacterSetData characterSet, 
        final Rect src
    ) {
        this.characterSet = characterSet;
        this.src = src;
    }

    public Bitmap bitmap() {
        return characterSet.bitmap();
    }
    
    public Rect src() {
        return src;
    }
}
