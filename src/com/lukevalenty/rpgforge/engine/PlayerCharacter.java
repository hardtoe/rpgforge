package com.lukevalenty.rpgforge.engine;

import com.lukevalenty.rpgforge.RpgForgeApplication;
import com.lukevalenty.rpgforge.data.CharacterData;
import com.lukevalenty.rpgforge.engine.input.GameInput;

public class PlayerCharacter extends GameObject {
    private double x, y;
    private double dx, dy;
    
    public PlayerCharacter() {
        final CharacterData charData = 
            RpgForgeApplication.getDb().getCharacterSets().get(0).getCharacters().get(0);
        
        // PLAYER CONTROL COMPONENT
        addComponent(new GameObjectComponent() {
            @Override
            public void update(
                final FrameState frameState, 
                final GameObject gameObject
            ) {
                if (frameState.phase == GamePhase.UPDATE) {
                    final GameInput g = 
                        frameState.globalState.getGameInput();
                    
                    final float timeDelta =
                        frameState.timeDelta;
                    
                    // FIXME: need to account for frame length instead
                    if (g.up()) {
                        dy = -timeDelta * 0.1;
                    
                    } else if (g.down()) {
                        dy = timeDelta * 0.1;
                        
                    } else {
                        dy = 0;
                    }
                    
                    if (g.left()) {
                        dx = -timeDelta * 0.1;
                        
                    } else if (g.right()) {
                        dx = timeDelta * 0.1;
                    
                    } else {
                        dx = 0;
                    }
                    
                    if (dx != 0 && dy != 0) {
                        dx = dx * 0.7071;
                        dy = dy * 0.7071;
                    }
                }
            }
        });
        
        // MOVE COMPONENT
        addComponent(new GameObjectComponent() {
            @Override
            public void update(
                final FrameState frameState, 
                final GameObject gameObject
            ) {
                if (frameState.phase == GamePhase.MOVE) {
                    x += dx;
                    y += dy;
                }
            }
        });
        
        /**
         * RENDER COMPONENT
         * 
         * Draws the appropriate character with animation depending on 
         * direction of travel.
         */
        addComponent(new GameObjectComponent() {
            private static final float FRAME_LENGTH_MS = 200;
            
            int yOffset = 0;
            int xOffset = 32;
            float frameTimer = 0;
            
            @Override
            public void update(
                final FrameState frameState, 
                final GameObject gameObject
            ) {
                if (frameState.phase == GamePhase.RENDER) {
                    frameTimer += frameState.timeDelta;
                    
                    if (dx == 0 && dy == 0) {
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
                    
                    if (dx < 0) {
                        // moving left
                        yOffset = 48;
                        
                    } else if (dx > 0) {
                        // moving right
                        yOffset = 96;
                        
                    } else {
                        if (dy < 0) {
                            // moving up
                            yOffset = 144;
                            
                        } else if (dy > 0) {
                            // moving down
                            yOffset = 0;
                        }
                    }
                    
                    frameState.drawBuffer.add(frameState.spritePool.get().set(
                        charData.bitmap(), 
                        yOffset, xOffset, 32 + xOffset, 48 + yOffset, 
                        (int) (16 + y), (int) (0 + x), (int) (32 + x), (int) (64 + y)));
                }
            }
        });
    }
}
