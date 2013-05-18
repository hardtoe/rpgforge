package com.lukevalenty.rpgforge.engine;

import android.graphics.Matrix;

public class CameraObject extends GameObject {

    public CameraObject(final float scaleFactor) {
        addComponent(new GameObjectComponent() {
            private Matrix m = new Matrix();
            
            {
                m.postScale(scaleFactor, scaleFactor);
            }
            
            @Override
            public void update(
                final FrameState frameState, 
                final GameObject gameObject
            ) {
                if (frameState.phase == GamePhase.PRERENDER) {
                    frameState.drawBuffer.add(frameState.setMatrixPool.get().set(m));
                }
            }
        });
    }
}
