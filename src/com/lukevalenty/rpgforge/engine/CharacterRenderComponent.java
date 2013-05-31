package com.lukevalenty.rpgforge.engine;

import com.lukevalenty.rpgforge.data.CharacterData;
import com.lukevalenty.rpgforge.engine.input.GameInput;

public class CharacterRenderComponent extends GameObjectComponent {
    private static final float FRAME_LENGTH_MS = 200;
    
    private CharacterData charData;
    private int yOffset = 0;
    private int xOffset = 32;
    private float frameTimer = 0;
    
    private NumberRef x;
    private NumberRef y;
    private ObjectRef<Direction> dir;
    private BooleanRef walking;

    private CharacterRenderComponent() {
        // do nothing
    }
    
    public CharacterRenderComponent(
        final GameObject o, 
        final CharacterData charData
    ) {
        this.charData = charData;
        
        this.x = o.getNumberRef("x");
        this.y = o.getNumberRef("y");
        this.dir = o.getObjectRef("dir");
        this.walking = o.getBooleanRef("walking");
    }

    @Override
    public void update(
        final FrameState frameState,
        final GlobalGameState globalState
    ) {
        if (frameState.phase == GamePhase.RENDER) {
            frameTimer += frameState.timeDelta;
            
            if (walking.value) {

                if (frameTimer >= FRAME_LENGTH_MS) {
                    if (frameTimer >= (FRAME_LENGTH_MS * 2)) {
                        // if a frame has been skipped, reset the frame
                        // timer.  this will ensure we don't have a ton
                        // of frames displayed quickly if we skipped a
                        // bunch of frames while sleeping.
                        frameTimer = 0;
                        
                    } else {
                        frameTimer = frameTimer - FRAME_LENGTH_MS;
                    } 
                    
                    if (xOffset == 0) {
                        xOffset = 64;
                        
                    } else {
                        xOffset = 0;
                    }
                }
                
            } else {
                xOffset = 32;
            }
            
            if (dir.value == Direction.LEFT) {
                yOffset = 48;
                
            } else if (dir.value == Direction.RIGHT) {
                yOffset = 96;
                
            } else if (dir.value == Direction.UP) {
                yOffset = 144;
                
            } else {
                yOffset = 0;
            }

            
            frameState.drawBuffer.add(frameState.spritePool.get().set(
                charData.bitmap(), 
                yOffset + charData.src().top, 
                xOffset + charData.src().left, 
                32 + xOffset + charData.src().left, 
                48 + yOffset + charData.src().top,
                
                (int) (16 + y.value), 
                (int) (0 + x.value), 
                (int) (32 + x.value), 
                (int) (64 + y.value)));
        }
    }

    public void setCharacterData(final CharacterData charData) {
        this.charData = charData;
    }

    public CharacterData getCharacterData() {
       return this.charData;
    }
}