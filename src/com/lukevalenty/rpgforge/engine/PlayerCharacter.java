package com.lukevalenty.rpgforge.engine;

import android.graphics.Point;

import com.lukevalenty.rpgforge.RpgForgeApplication;
import com.lukevalenty.rpgforge.data.CharacterData;
import com.lukevalenty.rpgforge.data.MapData;
import com.lukevalenty.rpgforge.data.TileData;
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
        
        // COLLISION COMPONENT
        addComponent(new GameObjectComponent() {
            private static final float NO_MOVEMENT = 0;
            private final Point upperLeft = new Point(2, 47);
            private final Point upperRight = new Point(30, 47);
            private final Point bottomLeft = new Point(2, 63);
            private final Point bottomRight = new Point(30, 63);
            
            private boolean hit(
                final MapData map, 
                final Point p,
                final double dx,
                final double dy
            ) {
                final TileData tile = map.getTile(
                    (int) ((x + p.x + dx) / 32), 
                    (int) ((y + p.y + dy) / 32));
                
                final TileData sparseTile = map.getSparseTile(
                    (int) ((x + p.x + dx) / 32), 
                    (int) ((y + p.y + dy) / 32));
                
                return 
                    tile == null || 
                    !tile.isPassable() ||
                    (sparseTile != null && !sparseTile.isPassable());
            }
            
            @Override
            public void update(
                final FrameState frameState, 
                final GameObject gameObject
            ) {
                if (frameState.phase == GamePhase.COLLISION) {
                    if (dx != 0 || dy != 0) {
                        final MapData map = 
                            frameState.globalState.getMap();

                        if (dx > 0) {
                            if (hit(map, upperRight, dx, 0) || hit(map, bottomRight, dx, 0)) {
                                dx = NO_MOVEMENT;
                            }
                            
                        } else if (dx < 0) {
                            if (hit(map, upperLeft, dx, 0) || hit(map, bottomLeft, dx, 0)) {
                                dx = NO_MOVEMENT;
                            }
                        }
                        
                        if (dy > 0) {
                            if (hit(map, bottomRight, 0, dy) || hit(map, bottomLeft, 0, dy)) {
                                dy = NO_MOVEMENT;
                            }
                        } else if (dy < 0) {
                            if (hit(map, upperRight, 0, dy) || hit(map, upperLeft, 0, dy)) {
                                dy = NO_MOVEMENT;
                            }
                        }
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
                    
                    frameState.globalState.setFocus((int) x, (int) y);
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
                    
                    if (g.left()) {
                        // moving left
                        yOffset = 48;
                        
                    } else if (g.right()) {
                        // moving right
                        yOffset = 96;
                        
                    } else {
                        if (g.up()) {
                            // moving up
                            yOffset = 144;
                            
                        } else if (g.down()) {
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
