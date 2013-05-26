package com.lukevalenty.rpgforge.engine;

import com.lukevalenty.rpgforge.data.CharacterData;
import com.lukevalenty.rpgforge.engine.input.GameInput;

public class CharacterRenderComponent extends GameObjectComponent {
    private static final float FRAME_LENGTH_MS = 200;
    
    private final CharacterData charData;
    int yOffset = 0;
    int xOffset = 32;
    float frameTimer = 0;
    
    private final NumberRef x;
    private final NumberRef y;
    private final ObjectRef<Direction> dir;

    public CharacterRenderComponent(
        final GameObject o, 
        final CharacterData charData
    ) {
        this.charData = charData;
        
        this.x = o.getNumberRef("x");
        this.y = o.getNumberRef("y");
        this.dir = o.getObjectRef("dir");
    }

    @Override
    public void update(
        final FrameState frameState
    ) {
        if (frameState.phase == GamePhase.RENDER) {
            final GameInput g = 
                frameState.globalState.getGameInput();
            
            frameTimer += frameState.timeDelta;
            
            if (!g.up() && !g.down() && !g.left() && !g.right()) {
                xOffset = 32;
                
            } else {
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
                yOffset, 
                xOffset, 
                32 + xOffset, 
                48 + yOffset,
                
                (int) (16 + y.value), 
                (int) (0 + x.value), 
                (int) (32 + x.value), 
                (int) (64 + y.value)));
        }
    }
}