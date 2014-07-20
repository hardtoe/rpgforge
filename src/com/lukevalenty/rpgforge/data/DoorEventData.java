package com.lukevalenty.rpgforge.data;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.lukevalenty.rpgforge.engine.ActivateMessage;
import com.lukevalenty.rpgforge.engine.BooleanRef;
import com.lukevalenty.rpgforge.engine.FrameState;
import com.lukevalenty.rpgforge.engine.GameMessage;
import com.lukevalenty.rpgforge.engine.GameObject;
import com.lukevalenty.rpgforge.engine.GameObjectComponent;
import com.lukevalenty.rpgforge.engine.GamePhase;
import com.lukevalenty.rpgforge.engine.GlobalGameState;
import com.lukevalenty.rpgforge.engine.NumberRef;
import com.lukevalenty.rpgforge.engine.ObjectRef;
import com.lukevalenty.rpgforge.engine.WalkOverMessage;

public class DoorEventData extends EventData {
    public static class DoorGameObject extends GameObjectComponent {
        private GameObject gameObject;
        private NumberRef doorWarpTargetX;
        private NumberRef doorWarpTargetY;
        private BooleanRef activeOnWalkOver;
        private ObjectRef<MapData> doorWarpTargetMap;        
        
        private transient GameObject activator = null;

        private DoorGameObject() {
            // do nothing and keep private
        }
        
        public DoorGameObject(final GameObject gameObject) {
            this.gameObject = gameObject;

            doorWarpTargetX = gameObject.getNumberRef("doorWarpTargetX");
            doorWarpTargetY = gameObject.getNumberRef("doorWarpTargetY");
            doorWarpTargetMap = gameObject.getObjectRef("doorWarpTargetMap");
            activeOnWalkOver = gameObject.getBooleanRef("activeOnWalkOver");
        }
        
        @Override
        public void update(
            final FrameState frameState, 
            final GlobalGameState globalState
        ) {
            if (frameState.phase == GamePhase.UPDATE) {
                if (activator != null) {
                    activator.getNumberRef("x").value = doorWarpTargetX.value;
                    activator.getNumberRef("y").value = doorWarpTargetY.value;
                    globalState.setMap(doorWarpTargetMap.value);
                    
                    activator = null;
                }
            }
        }

        @Override
        public void onMessage(final GameMessage msg) {
            if (msg instanceof WalkOverMessage && activeOnWalkOver.value) {
                activator = msg.sender();
                
            } else if (msg instanceof ActivateMessage && !activeOnWalkOver.value) {
                activator = msg.sender();
            }
        }
    }

    private final GameObject eventGameObject;

    public DoorEventData() {
        eventGameObject = new GameObject();             
        eventGameObject.addComponent(new DoorGameObject(eventGameObject));
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
                c.drawText("DOOR", 16, clipBounds.centerY() + 10, textPaint); 
                
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
        return eventGameObject;
    }

    @Override
    public EventData create() {
        return new DoorEventData();
    }
    
    public void setTarget(
        final int x, 
        final int y, 
        final MapData map,
        final boolean activeOnWalkOver
    ) {
        getGameObject().getNumberRef("doorWarpTargetX").value = x;
        getGameObject().getNumberRef("doorWarpTargetY").value = y;
        getGameObject().getObjectRef("doorWarpTargetMap").value = map;
        getGameObject().getBooleanRef("activeOnWalkOver").value = activeOnWalkOver;
    }
}
