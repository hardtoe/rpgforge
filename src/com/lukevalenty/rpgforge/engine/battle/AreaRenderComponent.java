package com.lukevalenty.rpgforge.engine.battle;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.lukevalenty.rpgforge.data.CharacterData;
import com.lukevalenty.rpgforge.engine.BooleanRef;
import com.lukevalenty.rpgforge.engine.Direction;
import com.lukevalenty.rpgforge.engine.FrameState;
import com.lukevalenty.rpgforge.engine.GameObject;
import com.lukevalenty.rpgforge.engine.GameObjectComponent;
import com.lukevalenty.rpgforge.engine.GamePhase;
import com.lukevalenty.rpgforge.engine.GlobalGameState;
import com.lukevalenty.rpgforge.engine.NumberRef;
import com.lukevalenty.rpgforge.engine.ObjectRef;

public class AreaRenderComponent extends GameObjectComponent {
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private int color;
    
    private transient Bitmap colorBitmap;
    
    private AreaRenderComponent() {
        // do nothing
    }
    
    public AreaRenderComponent(
        final GameObject o, 
        final int x1,
        final int y1,
        final int x2,
        final int y2,
        final int color
    ) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
    }

    @Override
    public void update(
        final FrameState frameState,
        final GlobalGameState globalState
    ) {
        if (frameState.phase == GamePhase.RENDER) {
            if (colorBitmap == null) {
                colorBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
                colorBitmap.eraseColor(color);
            }
            
            for (int x = x1; x < x2; x++) {
                for (int y = y1; y < y2; y++) {
                    frameState.drawBuffer.add(frameState.spritePool.get().set(
                        colorBitmap, 
                        
                        0, 
                        0, 
                        1, 
                        1, 
                        
                        y * 32, 
                        x * 32, 
                        (x + 1) * 32, 
                        (y + 1) * 32));
                }
            }
        }
    }

}