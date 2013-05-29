package com.lukevalenty.rpgforge.engine;

import android.graphics.Point;

import com.lukevalenty.rpgforge.data.MapData;
import com.lukevalenty.rpgforge.data.TileData;

public class CollisionComponent extends GameObjectComponent {
    private static final float NO_MOVEMENT = 0;
    private static final int PUSH_MOVEMENT = 4;
    
    private final Point upperLeft = new Point(2, 47);
    private final Point upperRight = new Point(30, 47);
    private final Point bottomLeft = new Point(2, 63);
    private final Point bottomRight = new Point(30, 63);
    
    private final NumberRef dx;
    private final NumberRef dy;
    private final NumberRef x;
    private final NumberRef y;

    public CollisionComponent(
        final GameObject o
    ) {
        this.dx = o.getNumberRef("dx");
        this.dy = o.getNumberRef("dy");
        this.x = o.getNumberRef("x");
        this.y = o.getNumberRef("y");
    }

    private boolean hit(
        final MapData map, 
        final Point p,
        final double dx,
        final double dy
    ) {
        final TileData tile = map.getTile(
            (int) ((x.value + p.x + dx) / 32), 
            (int) ((y.value + p.y + dy) / 32));
        
        final TileData sparseTile = map.getSparseTile(
            (int) ((x.value + p.x + dx) / 32), 
            (int) ((y.value + p.y + dy) / 32));
        
        return 
            tile == null || 
            !tile.isPassable() ||
            (sparseTile != null && !sparseTile.isPassable());
    }

    @Override
    public void update(
        final FrameState frameState,
        final GlobalGameState globalState
    ) {
        if (frameState.phase == GamePhase.COLLISION) {
            double pushDx = 0;
            double pushDy = 0;
            
            double outDx = dx.value;
            double outDy = dy.value;
            
            if (dx.value != 0 || dy.value != 0) {
                final MapData map = 
                    globalState.getMap();

                if (dx.value > 0) {
                    final boolean upperRightHit = hit(map, upperRight, dx.value, 0);
                    final boolean bottomRightHit = hit(map, bottomRight, dx.value, 0);
                    
                    if (upperRightHit || bottomRightHit) {
                        outDx = NO_MOVEMENT;
                        
                        if (dy.value == 0) {
                            if (!upperRightHit) {
                                pushDy = -PUSH_MOVEMENT;
                            
                            } else if (!bottomRightHit) {
                                pushDy = PUSH_MOVEMENT;
                            }      
                        }
                    }
                    
                } else if (dx.value < 0) {
                    final boolean upperLeftHit = hit(map, upperLeft, dx.value, 0);
                    final boolean bottomLeftHit = hit(map, bottomLeft, dx.value, 0);
                    
                    if (upperLeftHit || bottomLeftHit) {
                        outDx = NO_MOVEMENT;
                        
                        if (dy.value == 0) {
                            if (!upperLeftHit) {
                                pushDy = -PUSH_MOVEMENT;
                            
                            } else if (!bottomLeftHit) {
                                pushDy = PUSH_MOVEMENT;
                            }      
                        }
                    }
                }
                
                if (dy.value > 0) {
                    final boolean bottomRightHit = hit(map, bottomRight, 0, dy.value);
                    final boolean bottomLeftHit = hit(map, bottomLeft, 0, dy.value);
                    
                    if (bottomRightHit || bottomLeftHit) {
                        outDy = NO_MOVEMENT;
                        
                        if (dx.value == 0) {
                            if (!bottomRightHit) {
                                pushDx = PUSH_MOVEMENT;
                            
                            } else if (!bottomLeftHit) {
                                pushDx = -PUSH_MOVEMENT;
                            }      
                        } 
                    }
                    
                } else if (dy.value < 0) {
                    final boolean upperRightHit = hit(map, upperRight, 0, dy.value);
                    final boolean upperLeftHit = hit(map, upperLeft, 0, dy.value);
                    
                    if (upperRightHit || upperLeftHit) {
                        outDy = NO_MOVEMENT;
                        
                        if (dx.value == 0) {
                            if (!upperRightHit) {
                                pushDx = PUSH_MOVEMENT;
                            
                            } else if (!upperLeftHit) {
                                pushDx = -PUSH_MOVEMENT;
                            }      
                        }
                    }
                }
            }
            
            dx.value = outDx + pushDx;
            dy.value = outDy + pushDy;
        }
    }
}