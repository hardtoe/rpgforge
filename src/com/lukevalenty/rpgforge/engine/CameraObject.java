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

                    final GlobalGameState global = frameState.globalState;
                    
                    // center of 512 x 384 screen
                    final int xFocus = 256 - global.getXFocus();
                    final int yFocus = 192 - global.getYFocus();
                    
                    m.postTranslate(
                        Math.max(Math.min(xFocus, 0), 512 - (global.getMap().getWidth() * 32)), 
                        Math.max(Math.min(yFocus, 0), 384 - (global.getMap().getHeight() * 32)));
                    
                    m.postScale(scaleFactor, scaleFactor);
                    
                    
                    
                    frameState.drawBuffer.add(frameState.setMatrixPool.get().set(m));
                }
            }
        });
    }
}
