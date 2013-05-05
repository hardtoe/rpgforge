package com.lukevalenty.rpgforge.graphics;

import android.graphics.Matrix;

import com.lukevalenty.rpgforge.data.MapData;
import com.lukevalenty.rpgforge.memory.ObjectPool;

public class DrawTileMap extends DrawCommand<DrawTileMap> {
    public DrawTileMap(final ObjectPool<DrawTileMap> objectPool) {
        super(objectPool);
        this.matrix = new Matrix();
    }
    
    private MapData map;
    private Matrix matrix;
    
    public DrawTileMap set(
        final MapData map,
        final Matrix matrix
    ) {
        super.init();
        
        this.map = map;
        this.matrix.set(matrix);
        
        return this;
    }
    
    public MapData map() {
        return map;
    }

    public Matrix matrix() {
        return matrix;
    }
    
    @Override
    public void recycle() {
        this.map = null;
        super.recycle();
    }
}
