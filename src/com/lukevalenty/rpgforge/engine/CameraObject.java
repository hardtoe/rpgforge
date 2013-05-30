package com.lukevalenty.rpgforge.engine;

import java.util.ArrayList;

import com.lukevalenty.rpgforge.data.MapData;
import com.lukevalenty.rpgforge.graphics.DrawCommand;
import com.lukevalenty.rpgforge.graphics.SetMatrix;
import com.lukevalenty.rpgforge.graphics.SetMatrixPool;

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
                final FrameState frameState,
                final GlobalGameState globalState
            ) {
                if (frameState.phase == GamePhase.PRERENDER) {
                    m.reset();

                    final MapData map = globalState.getMap();
                    
                    float xTranslate;
                    float yTranslate;
                    
                    final int mapWidthPixels = map.getWidth() * 32;
                    
                    if (mapWidthPixels > SCREEN_WIDTH) {
                        final int xFocus = (SCREEN_WIDTH / 2) - globalState.getXFocus();
                        xTranslate = Math.max(Math.min(xFocus, 0), SCREEN_WIDTH - mapWidthPixels);
                    
                    } else {
                        xTranslate = (SCREEN_WIDTH - mapWidthPixels) / 2;
                    }
                    
                    final int mapHeightPixels = map.getHeight() * 32;
                    
                    if (mapHeightPixels > SCREEN_HEIGHT) {
                        final int yFocus = (SCREEN_HEIGHT / 2) - globalState.getYFocus();
                        yTranslate = Math.max(Math.min(yFocus, 0), SCREEN_HEIGHT - mapHeightPixels);
                        
                    } else {
                        yTranslate = (SCREEN_HEIGHT - mapHeightPixels) / 2;
                    }
                    
                    
                    
                    m.postTranslate(xTranslate, yTranslate);
                    m.postScale(scaleFactor, scaleFactor);
                   
                    final ArrayList<DrawCommand> drawBuffer = frameState.drawBuffer;
                    
                    final SetMatrixPool setMatrixPool = frameState.setMatrixPool;
                    
                    final SetMatrix setMatrix = setMatrixPool.get();
                    
                    setMatrix.set(m);
                    
                    drawBuffer.add(setMatrix);
                }
            }
        });
    }
}
