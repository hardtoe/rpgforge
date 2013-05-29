package com.lukevalenty.rpgforge.engine;

import com.lukevalenty.rpgforge.engine.input.GameInput;

public class PlayerControlComponent extends GameObjectComponent {
    public static final double WALK_SPEED = 0.14;

    private final GameObject gameObject;
    
    private final NumberRef x;
    private final NumberRef y;
    private final NumberRef dx;
    private final NumberRef dy;
    private final ObjectRef<Direction> dir;
    private final BooleanRef walking;

    public PlayerControlComponent(
        final GameObject o
    ) {
        this.gameObject = o;
        this.x = o.getNumberRef("x");
        this.y = o.getNumberRef("y");
        this.dx = o.getNumberRef("dx");
        this.dy = o.getNumberRef("dy");
        this.dir = o.getObjectRef("dir");
        this.walking = o.getBooleanRef("walking");
    }
    
    @Override
    public void update(
        final FrameState frameState,
        final GlobalGameState globalState
    ) {
        if (frameState.phase == GamePhase.UPDATE) {
            final GameInput g = 
                globalState.getGameInput();
            
            final float timeDelta =
                frameState.timeDelta;
            
            // COMPUTE MOVEMENT DIRECTION AND MAGNITUDE
            if (g.up()) {
                dy.value = -timeDelta * WALK_SPEED;

            } else if (g.down()) {
                dy.value = timeDelta * WALK_SPEED;
                
            } else {
                dy.value = 0;
            }
            
            if (g.left()) {
                dx.value = -timeDelta * WALK_SPEED;
                
            } else if (g.right()) {
                dx.value = timeDelta * WALK_SPEED;
            
            } else {
                dx.value = 0;
            }
            
            if (dx.value != 0 && dy.value != 0) {
                dx.value = dx.value * 0.7071;
                dy.value = dy.value * 0.7071;
            }
            
            // COMPUTE DIRECTION CHARACTER IS FACING
            if (g.up()) {
                if (dir.value == Direction.DOWN) {
                    dir.value = Direction.UP;
                    
                } else if (g.left()) {
                    if (dir.value == Direction.RIGHT) {
                        dir.value = Direction.LEFT;
                    }
                    
                } else if (g.right()) {
                    if (dir.value == Direction.LEFT) {
                        dir.value = Direction.RIGHT;
                    }
                
                } else {
                    dir.value = Direction.UP;
                }
                
            } else if (g.down()) {
                if (dir.value == Direction.UP) {
                    dir.value = Direction.DOWN;
                    
                } else if (g.left()) {
                    if (dir.value == Direction.RIGHT) {
                        dir.value = Direction.LEFT;
                    }
                    
                } else if (g.right()) {
                    if (dir.value == Direction.LEFT) {
                        dir.value = Direction.RIGHT;
                    }
                    
                } else {
                    dir.value = Direction.DOWN;
                }
                
            } else {
                if (g.left()) {
                    dir.value = Direction.LEFT;
                    
                } else if (g.right()) {
                    dir.value = Direction.RIGHT;
                    
                } else {
                    // keep facing previous direction
                }
            }
            
            // EVENT ACTIVATION
            if (g.action()) {
                final int xTile = 
                    (int) (((x.value + 16) / 32) + dir.value.x);
                
                final int yTile = 
                    (int) (((y.value + 48)/ 32) + dir.value.y);
                
                globalState.activate(gameObject, xTile, yTile);
            }
            
            // SET WALKING
            walking.value = g.left() || g.right() || g.up() || g.down();
        }
    }
}