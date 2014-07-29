package com.lukevalenty.rpgforge.engine.battle;


import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;

import com.lukevalenty.rpgforge.data.EnemyCharacterData;
import com.lukevalenty.rpgforge.data.EventData;
import com.lukevalenty.rpgforge.data.PlayerCharacterData;
import com.lukevalenty.rpgforge.engine.GameObject;
import com.lukevalenty.rpgforge.engine.ObjectRef;

public class BattleZoneEventData extends EventData {
    private GameObject battleZoneGameObject;
    private BattleZoneComponent battleZoneComponent;
    
    
    public BattleZoneEventData() {
        battleZoneGameObject = 
            new GameObject();      

        battleZoneComponent = 
            new BattleZoneComponent();
        
        battleZoneGameObject.addComponent(battleZoneComponent);
    }

    @Override
    protected Drawable createPreview() {
        final Paint textPaint =
            new Paint();
        
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(25);
        
        final Paint linePaint =
            new Paint();
        
        linePaint.setColor(Color.WHITE);
        linePaint.setStyle(Style.STROKE);
        linePaint.setStrokeWidth(3);
        
        return new Drawable() {
            @Override
            public void draw(
                final Canvas c
            ) {
                Rect clipBounds = 
                    c.getClipBounds();
                
                c.drawColor(Color.BLACK);
                c.drawText("BATTLE", 16, clipBounds.centerY() + 10, textPaint); 
                
                c.drawRect(0, 0, clipBounds.width() - 6, clipBounds.height() - 6, linePaint);
            }

            @Override
            public int getOpacity() {
                return PixelFormat.TRANSPARENT;
            }

            @Override
            public void setAlpha(
                final int alpha
            ) {
                // do nothing
            }

            @Override
            public void setColorFilter(
                final ColorFilter cf
            ) {
                // do nothing
            }
        };
    }

    @Override
    public GameObject getGameObject() {
        return battleZoneGameObject;
    }

    @Override
    public EventData create() {
        return new BattleZoneEventData();
    }    
    
    public void setBattleArea(
        final int x1, 
        final int y1,
        final int x2,
        final int y2
    ) {
        battleZoneComponent.setBattleArea(x1, y1, x2, y2);
    }
    
    public int getX1() {
        return battleZoneComponent.getX1();
    }
    
    public int getY1() {
        return battleZoneComponent.getY1();
    }
    
    public int getX2() {
        return battleZoneComponent.getX2();
    }
    
    public int getY2() {
        return battleZoneComponent.getY2();
    }
}
