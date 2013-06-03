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

import com.lukevalenty.rpgforge.engine.BooleanRef;
import com.lukevalenty.rpgforge.engine.CharacterRenderComponent;
import com.lukevalenty.rpgforge.engine.CollisionComponent;
import com.lukevalenty.rpgforge.engine.Direction;
import com.lukevalenty.rpgforge.engine.FrameState;
import com.lukevalenty.rpgforge.engine.GameObject;
import com.lukevalenty.rpgforge.engine.GameObjectComponent;
import com.lukevalenty.rpgforge.engine.GamePhase;
import com.lukevalenty.rpgforge.engine.GlobalGameState;
import com.lukevalenty.rpgforge.engine.MovementComponent;
import com.lukevalenty.rpgforge.engine.NumberRef;
import com.lukevalenty.rpgforge.engine.ObjectRef;
import com.lukevalenty.rpgforge.engine.RandomWalkComponent;

public class NpcEventData extends EventData {
    private CharacterRenderComponent characterRenderComponent;
    
    public static class NpcGameObject extends GameObjectComponent {
        private GameObject gameObject;
        private ObjectRef<String> characterName;  
        private ObjectRef<String> characterDialog;     
        private NumberRef tileX;
        private NumberRef tileY;  
        private NumberRef x;
        private NumberRef y;
        
        private transient GameObject activator = null;

        private NpcGameObject() {
            // do nothing and keep private
        }
        
        public NpcGameObject(final GameObject gameObject) {
            this.gameObject = gameObject;
            
            characterName = gameObject.getObjectRef("characterName");
            characterDialog = gameObject.getObjectRef("characterDialog");
            tileX = gameObject.getNumberRef("tileX");
            tileY = gameObject.getNumberRef("tileY");
            x = gameObject.getNumberRef("x");
            y = gameObject.getNumberRef("y");
        }
        
        @Override
        public void update(
            final FrameState frameState, 
            final GlobalGameState globalState
        ) {
            if (frameState.phase == GamePhase.UPDATE) {
                // FIXME: find better place to put this...maybe init function?
                if (
                    tileX == null || 
                    tileY == null || 
                    x == null || 
                    y == null
                ) {
                    tileX = gameObject.getNumberRef("tileX");
                    tileY = gameObject.getNumberRef("tileY");
                    x = gameObject.getNumberRef("x");
                    y = gameObject.getNumberRef("y");
                }
                
                tileX.value = (int) (x.value / 32);
                tileY.value = (int) ((y.value + 32) / 32);
                
            } else if (frameState.phase == GamePhase.RENDER) {
                if (activator != null) {
                    if (showingDialog) {
                        if ((System.nanoTime() - dialogTime) > 1000000000L) {
                            dialogTime = System.nanoTime();
                            showingDialog = false;
                        }
                        
                    } else if ((System.nanoTime() - dialogTime) > 1000000000L) {
                        dialogTime = System.nanoTime();
                        showingDialog = true;
                    }
                    
                    activator = null;
                }
                
                if (showingDialog) {
                    frameState.drawBuffer.add(frameState.dialogPool.get().set(dialogText()).setZ(1000));
                }
            }
        }

        private transient long dialogTime = 0;
        private transient boolean showingDialog = false;
        
        private transient String dialogText;
        
        private String dialogText() {
            if (dialogText == null) {
                dialogText = characterName.value + ": " + characterDialog.value;
            }
            
            return dialogText;
        }

        @Override
        public void activate(final GameObject sender) {
            activator = sender;
        }
    }

    private final GameObject eventGameObject;
    private int initialX;
    private int initialY;
    private boolean stationary;
    private Direction initialDirection;

    public NpcEventData() {
        eventGameObject = 
            new GameObject();      
        
        eventGameObject.addComponent(new NpcGameObject(eventGameObject));

        eventGameObject.addComponent(new RandomWalkComponent(eventGameObject));
        eventGameObject.addComponent(new CollisionComponent(eventGameObject));
        eventGameObject.addComponent(new MovementComponent(eventGameObject));
        
        characterRenderComponent = 
            new CharacterRenderComponent(eventGameObject, null);
        
        eventGameObject.addComponent(characterRenderComponent);
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
                c.drawText("NPC", 16, clipBounds.centerY() + 10, textPaint); 
                
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
        eventGameObject.getNumberRef("x").value = initialX;
        eventGameObject.getNumberRef("y").value = initialY;
        eventGameObject.getBooleanRef("stationary").value = stationary;
        eventGameObject.getObjectRef("dir").value = initialDirection;
        return eventGameObject;
    }

    @Override
    public EventData create() {
        return new NpcEventData();
    }
    
    public void setCharacterName(final String name) {
        eventGameObject.getObjectRef("characterName").value = name;
    }
    
    public void setCharacterDialog(final String dialog) {
        eventGameObject.getObjectRef("characterDialog").value = dialog;
    }
    
    public void setCharacterData(final CharacterData charData) {
        characterRenderComponent.setCharacterData(charData);
    }

    public void setInitialPosition(int x, int y) {
        initialX = x;
        initialY = y;
    }
    
    public CharacterData getCharacterData() {
        return characterRenderComponent.getCharacterData();
    }

    public void setStationary(final boolean stationary) {
        this.stationary = stationary;
    }

    public void setDirection(final Direction initialDirection) {
        this.initialDirection = initialDirection;
    }
}
