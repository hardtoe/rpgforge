package com.lukevalenty.rpgforge.data;

public class SpriteSetData extends BitmapData {
    @SuppressWarnings("unused")
    private SpriteSetData() {
        // default constructor needed for serialization
        super();
    }
    
    public SpriteSetData(
        final String bitmapFilePath
    ) {
        super(bitmapFilePath);
    }
}
