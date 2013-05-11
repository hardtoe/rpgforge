package com.lukevalenty.rpgforge.graphics;

import com.lukevalenty.rpgforge.data.MapData;
import com.lukevalenty.rpgforge.memory.ObjectPool;

public class DrawTileMap extends DrawCommand<DrawTileMap> {
    public DrawTileMap(final ObjectPool<DrawTileMap> objectPool) {
        super(objectPool);
    }
    
    private MapData map;
    
    public DrawTileMap set(
        final MapData map
    ) {
        super.init();
        
        this.map = map;
        
        return this;
    }
    
    public MapData map() {
        return map;
    }
    
    @Override
    public void recycle() {
        this.map = null;
        super.recycle();
    }
}
