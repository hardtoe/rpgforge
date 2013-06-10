package com.lukevalenty.rpgforge.data;

import com.lukevalenty.rpgforge.editor.map.PaletteItem;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

// TODO: need universal compatibility flag
// TODO: convert boolean to int flags
public abstract class TileData implements PaletteItem {
    protected TileSetData tileset;
    protected Rect[] src;
    protected int frameDelay;
    protected boolean passable = true;
    protected int[] frames;
    protected int layer = 0;
    
    
    protected transient int avgColor = 0;
    protected transient Drawable preview = null;
    
    protected TileData() {
        // default constructor needed for serialization
        this.frameDelay = 16;
    }
    
    public TileData(
        final TileSetData tileset,
        final Rect... src
    ) {
        this();
        this.tileset = tileset;
        this.src = src;
    }
    
    public Rect src(long frameIndex) {
        if (frames != null) {
            return src[frames[(int) ((frameIndex / frameDelay) % frames.length)]];
            
        } else {
            return src[(int) ((frameIndex / frameDelay) % src.length)];
        }
    }
    
    public Bitmap bitmap() {
        return tileset.bitmap();
    }
    
    public TileData setFrameDelay(final int delay) {
        this.frameDelay = delay;
        return this;
    }
    
    public TileData setAnimationSequence(final int... frames) {
        this.frames = frames;
        return this;
    }
    
    public TileData setPassable(final boolean passable) {
        this.passable = passable;
        return this;
    }
    
    public TileData setLayer(final int layer) {
        this.layer = layer;
        return this;
    }
    
    public boolean isPassable() {
        return passable;
    }
    
    public Drawable getPreview() {
        if (preview == null) {
            preview = 
                new Drawable() {
                    @Override
                    public void draw(final Canvas canvas) {
                        canvas.drawBitmap(bitmap(), src[0], getBounds(), null);
                    }
    
                    @Override
                    public int getOpacity() {
                        return PixelFormat.OPAQUE;
                    }
    
                    @Override
                    public void setAlpha(final int alpha) {
                        // do nothing
                    }
    
                    @Override
                    public void setColorFilter(final ColorFilter cf) {
                        // do nothing
                    }
                };
        }
        
        return preview;
    }
    
    public int getLayer() {
        return layer;
    }
    
    public int getAvgColor() {
        int numPixels = 1; // protect against divide by zero
        
        int r = 0;
        int g = 0;
        int b = 0;
        
        final int yMax = 
            Math.min(src[0].bottom, bitmap().getHeight());
        
        final int xMax =
            Math.min(src[0].right, bitmap().getWidth());
        
        if (avgColor == 0) {
            for (int y = src[0].top; y < yMax; y++) {
                for (int x = src[0].left; x < xMax; x++) {

                    final int pixel = 
                        bitmap().getPixel(x, y);
                    
                    final int alpha = 
                        (pixel >> 24) & 0xff;
                    
                    if (alpha > 0x80) {    
                        numPixels++;
                        
                        r += (pixel >> 16) & 0xff;
                        g += (pixel >> 8) & 0xff;
                        b += (pixel) & 0xff;
                    }
                }
            }
            
            avgColor = Color.rgb(r / numPixels, g / numPixels, b / numPixels);
        }
        
        return avgColor;
    }
}
