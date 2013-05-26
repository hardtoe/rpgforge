package com.lukevalenty.rpgforge.engine;

import com.lukevalenty.rpgforge.data.MapData;

import android.graphics.Matrix;

public class CameraObject extends GameObject {
    private static final int SCREEN_WIDTH = 512;
    private static final int SCREEN_HEIGHT = 384;
    
    public CameraObject(
        final float scaleFactor
    ) {
        addComponent(new GameObjectComponent() {
            private Matrix m = new Matrix();
            
            @Override
            public void update(
                final FrameState frameState
            ) {
                if (frameState.phase == GamePhase.PRERENDER) {
                    m.reset();

                    final GlobalGameState global = frameState.globalState;
                    final MapData map = global.getMap();
                    
                    float xTranslate;
                    float yTranslate;
                    
                    final int mapWidthPixels = map.getWidth() * 32;
                    
                    if (mapWidthPixels > SCREEN_WIDTH) {
                        final int xFocus = (SCREEN_WIDTH / 2) - global.getXFocus();
                        xTranslate = Math.max(Math.min(xFocus, 0), SCREEN_WIDTH - mapWidthPixels);
                    
                    } else {
                        xTranslate = (SCREEN_WIDTH - mapWidthPixels) / 2;
                    }
                    
                    final int mapHeightPixels = map.getHeight() * 32;
                    
                    if (mapHeightPixels > SCREEN_HEIGHT) {
                        final int yFocus = (SCREEN_HEIGHT / 2) - global.getYFocus();
                        yTranslate = Math.max(Math.min(yFocus, 0), SCREEN_HEIGHT - mapHeightPixels);
                        
                    } else {
                        yTranslate = (SCREEN_HEIGHT - mapHeightPixels) / 2;
                    }
                    
                    
                    
                    m.postTranslate(xTranslate, yTranslate);
                    m.postScale(scaleFactor, scaleFactor);
                   
                    frameState.drawBuffer.add(frameState.setMatrixPool.get().set(m));
                }
            }
        });
    }
}
