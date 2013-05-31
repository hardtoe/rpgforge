package com.lukevalenty.rpgforge.graphics;

import android.graphics.Matrix;

import com.lukevalenty.rpgforge.data.MapData;
import com.lukevalenty.rpgforge.memory.ObjectPool;

public class SetMatrix extends DrawCommand<SetMatrix> {
    public SetMatrix(final ObjectPool<SetMatrix> objectPool) {
        super(objectPool);
        this.matrix = new Matrix();
    }
    
    private Matrix matrix;
    
    public SetMatrix set(
        final Matrix matrix
    ) {
        super.init();
        
        this.matrix.set(matrix);
        
        return this;
    }

    public Matrix matrix() {
        return matrix;
    }
    
    @Override
    public void recycle() {
        super.recycle();
    }
    
    @Override
    public final float z() {
        return -2000;
    }
}
