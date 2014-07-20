package com.lukevalenty.rpgforge.engine.battle;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;

import com.lukevalenty.rpgforge.data.EventData;
import com.lukevalenty.rpgforge.engine.GameObject;

public class BattleZoneEventData extends EventData {
    private GameObject battleZoneGameObject;
    
    public BattleZoneEventData() {
        battleZoneGameObject = 
            new GameObject();      

        battleZoneGameObject.addComponent(new BattleZoneComponent());
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
        getGameObject().getNumberRef("x1").value = x1;
        getGameObject().getNumberRef("y1").value = y1;
        getGameObject().getNumberRef("x2").value = x2;
        getGameObject().getNumberRef("y2").value = y2;
    }
    
    public int getX1() {
        return (int) getGameObject().getNumberRef("x1").value;
    }
    
    public int getY1() {
        return (int) getGameObject().getNumberRef("y1").value;
    }
    
    public int getX2() {
        return (int) getGameObject().getNumberRef("x2").value;
    }
    
    public int getY2() {
        return (int) getGameObject().getNumberRef("y2").value;
    }
}
