package com.lukevalenty.rpgforge.data;

import com.lukevalenty.rpgforge.editor.map.PaletteItem;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class BattleCharacterData implements PaletteItem {
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

    @Override
    public Drawable getPreview() {
        final Rect dst = new Rect();      

        final Rect src = new Rect();
        src.left = getCharacterData().src().left + 32;
        src.top = getCharacterData().src().top;
        src.right = getCharacterData().src().right - 32;
        src.bottom = getCharacterData().src().top + 48;
        
        final Paint paint = new Paint();     
        paint.setFilterBitmap(false);
        
        return new Drawable() {
            @Override
            public void setColorFilter(ColorFilter cf) {
                // do nothing
            }
            
            @Override
            public void setAlpha(int alpha) {
                // do nothing
            }
            
            @Override
            public int getOpacity() {
                return PixelFormat.TRANSLUCENT;
            }
            
            @Override
            public void draw(Canvas canvas) {
                int height = getBounds().height();
                int width = (height * 2) / 3;
                
                dst.top = getBounds().top;
                dst.bottom = getBounds().bottom;
                dst.left = (getBounds().width() - width) / 2;
                dst.right = dst.left + width;
                canvas.drawBitmap(getCharacterData().bitmap(), src, dst, paint);
            }
        };
    }
}
