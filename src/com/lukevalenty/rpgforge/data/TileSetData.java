package com.lukevalenty.rpgforge.data;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class TileSetData extends BitmapData {
    @SuppressWarnings("unused")
    private TileSetData() {
        // default constructor needed for serialization
        super();
    }
    
    public TileSetData(
        final String bitmapFilePath
    ) {
        super(bitmapFilePath);
    }
}
