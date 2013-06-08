package com.lukevalenty.rpgforge.data;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;

public class CharacterSetData extends BitmapData {
    private ArrayList<CharacterData> characters = new ArrayList<CharacterData>();
    
    @SuppressWarnings("unused")
    private CharacterSetData() {
        // default constructor needed for serialization
        super();
    }
    
    public CharacterSetData(
        final String bitmapFilePath
    ) {
        super(bitmapFilePath);
    }

    public void load(final Context context) {
        super.load(context);
        
        /*
        // THIS WAS A STUPID HACK TO MAKE THE SPRITESHEETS TRANSPARENT
        
        int transparentColor = bitmap().getPixel(0, 0);
        
        final Bitmap newBitmap = 
            Bitmap.createBitmap(bitmap().getWidth(), bitmap().getHeight(), Config.ARGB_8888);
        
        for (int y = 0; y < bitmap().getHeight(); y++) {
            for (int x = 0; x < bitmap().getWidth(); x++) {
                if (bitmap().getPixel(x, y) == transparentColor) {
                    newBitmap.setPixel(x, y, Color.TRANSPARENT);
                
                } else {
                    newBitmap.setPixel(x, y, bitmap().getPixel(x, y));
                }
            }
        }
        
        this.bitmap.recycle();
        this.bitmap = null;
        this.bitmap = newBitmap;
        
        

        
        FileOutputStream thumbnailOutput;
        try {
            thumbnailOutput = new FileOutputStream(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/Screenshots/" + bitmapFilePath);

            bitmap.compress(CompressFormat.PNG, 0, thumbnailOutput);
            
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        */
        
    }

    public void addCharacter(final CharacterData characterData) {
        characters.add(characterData);
    }

    public List<CharacterData> getCharacters() {
        return characters;
    }
}
