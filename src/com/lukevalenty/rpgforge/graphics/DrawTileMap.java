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
    
    public DrawTileMap set(
        final MapData map, 
        final Bitmap bitmap
    ) {
        super.init();
        
        this.map = map;
        this.bitmap = bitmap;
        
        return this;
    }
    
    public DrawTileMap set(
        final MapData map
    ) {
        super.init();
        
        this.map = map;
        this.bitmap = null;
        
        return this;
    }
    
    public MapData map() {
        return map;
    }
    
    public Bitmap bitmap() {
        return bitmap;
    }
    
    @Override
    public void recycle() {
        this.map = null;
        super.recycle();
    }
}
