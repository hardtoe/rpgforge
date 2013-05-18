package com.lukevalenty.rpgforge.engine;

import com.lukevalenty.rpgforge.RpgForgeApplication;
import com.lukevalenty.rpgforge.data.CharacterData;
import com.lukevalenty.rpgforge.engine.input.GameInput;

public class PlayerCharacter extends GameObject {
    private double x, y;
    
    public PlayerCharacter() {
        final CharacterData charData = 
            RpgForgeApplication.getDb().getCharacterSets().get(0).getCharacters().get(0);
        
        addComponent(new GameObjectComponent() {
            @Override
            public void update(
                final FrameState frameState, 
                final GameObject gameObject
            ) {
                if (frameState.phase == GamePhase.RENDER) {
                    frameState.drawBuffer.add(frameState.spritePool.get().set(
                        charData.bitmap(), 
                        0, 0, 32, 48, 
                        (int) (16 + y), (int) (0 + x), (int) (32 + x), (int) (64 + y)));
                }
            }
        });
        
        addComponent(new GameObjectComponent() {
            @Override
            public void update(
                final FrameState frameState, 
                final GameObject gameObject
            ) {
                final GameInput g = 
                    frameState.globalState.getGameInput();
                
                final float timeDelta =
                    frameState.timeDelta;
                
                // FIXME: need to account for frame length instead
                if (g.up()) {
                    y -= timeDelta * 0.02;
                
                } else if (g.down()) {
                    y += timeDelta * 0.02;
                }
                
                if (g.left()) {
                    x -= timeDelta * 0.02;
                } else if (g.right()) {
                    x += timeDelta * 0.02;
                }
            }
            
        });
    }
}
