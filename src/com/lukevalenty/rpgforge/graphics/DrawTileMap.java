package com.lukevalenty.rpgforge.graphics;

import android.graphics.Bitmap;

import com.lukevalenty.rpgforge.data.MapData;
import com.lukevalenty.rpgforge.memory.ObjectPool;

public class DrawTileMap extends DrawCommand<DrawTileMap> {
    public DrawTileMap(final ObjectPool<DrawTileMap> objectPool) {
        super(objectPool);
    }
    
    private MapData map;
    private Bitmap bitmap;
    private boolean upper;
    private boolean lower;
    
    public DrawTileMap set(
        final MapData map, 
        final Bitmap bitmap
    ) {
        init();
        
        this.map = map;
        this.bitmap = bitmap;
        
        return this;
    }
    
    public DrawTileMap set(
        final MapData map
    ) {
        init();
        
        this.map = map;
        this.bitmap = null;
        
        return this;
    }
    
    public void init() {
        super.init();
        this.upper = false;
        this.lower = false;
    }
    
    public MapData map() {
        return map;
    }
    
    public Bitmap bitmap() {
        return bitmap;
    }
    
    public DrawTileMap setUpper() {
        this.upper = true;
        return this;
    }
    
    public DrawTileMap setLower() {
        this.lower = true;
        return this;
    }
    
    public boolean isUpper() {
        return this.upper;
    }
    
    public boolean isLower() {
        return this.lower;
    }
    
    @Override
    public void recycle() {
        this.map = null;
        super.recycle();
    }
}
