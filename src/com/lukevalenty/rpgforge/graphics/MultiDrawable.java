package com.lukevalenty.rpgforge.graphics;

import java.util.LinkedList;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class MultiDrawable extends Drawable {
    private final LinkedList<Drawable> drawables =
        new LinkedList<Drawable>();
    
    public MultiDrawable(final Drawable... drawables) {
        for (Drawable drawable : drawables) {
            this.drawables.add(drawable);
        }
    }
    
    @Override
    public void draw(Canvas canvas) { 
        for (Drawable drawable : drawables) {
            drawable.setBounds(getBounds());
            drawable.draw(canvas);
        }
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        // do nothing
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // do nothing
    }
}
