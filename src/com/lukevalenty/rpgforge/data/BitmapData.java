package com.lukevalenty.rpgforge.data;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public abstract class BitmapData {
    private String bitmapFilePath;
    
    private transient Bitmap bitmap;
    
    protected BitmapData() {
        // default constructor needed for serialization
    }
    
    public BitmapData(
        final String bitmapFilePath
    ) {
        this.bitmapFilePath = bitmapFilePath;
    }
    
    public Bitmap bitmap() {
        return bitmap;
    }
    
    public void load(final Context context) {
        try {
            InputStream ims = context.getAssets().open(bitmapFilePath);
            bitmap = BitmapFactory.decodeStream(ims);
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }   
    }
    
    public void unload() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }
}
