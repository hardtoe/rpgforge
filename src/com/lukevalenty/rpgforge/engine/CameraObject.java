package com.lukevalenty.rpgforge.engine;

import android.graphics.Matrix;

public class CameraObject extends GameObject {
    public CameraObject(
        final float scaleFactor
    ) {
        addComponent(new GameObjectComponent() {
            private Matrix m = new Matrix();
            
            @Override
            public void update(
                final FrameState frameState, 
                final GameObject gameObject
            ) {
                if (frameState.phase == GamePhase.PRERENDER) {
                    m.reset();
                    
                    // center of 512 x 384 screen
                    m.postTranslate(
                        (256 - 16) - frameState.globalState.getXFocus(), 
                        (192 - 32) - frameState.globalState.getYFocus());
                    
                    m.postScale(scaleFactor, scaleFactor);
                    
                    
                    
                    frameState.drawBuffer.add(frameState.setMatrixPool.get().set(m));
                }
            }
        });
    }
}
