package com.lukevalenty.rpgforge.engine;

public class MovementComponent extends GameObjectComponent {
    private transient NumberRef dx;
    private transient NumberRef dy;
    private transient NumberRef dxOld;
    private transient NumberRef dyOld;
    private transient NumberRef x;
    private transient NumberRef y;
    private transient ObjectRef<Object> dir;
    private transient BooleanRef walking;

    public MovementComponent() {
        // do nothing
    }
    
    public void init(
        final GameObject o, 
        final GlobalGameState globalState
    ) {
        this.dx = o.getNumberRef("dx");
        this.dy = o.getNumberRef("dy");
        this.dxOld = o.getNumberRef("dxOld");
        this.dyOld = o.getNumberRef("dyOld");
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
        if (frameState.phase == GamePhase.MOVE) {
            // UPDATE CHARACTER LOCATION
            x.value = Math.max(0, x.value + dx.value);
            y.value = Math.max(-16, y.value + dy.value);

            // SET WALKING
            walking.value = dx.value != 0 || dy.value != 0;
            
            // COMPUTE DIRECTION CHARACTER IS FACING
            if (dir.value == null) {
                dir.value = Direction.DOWN;
            }
            
            if (dyOld.value < 0) {
                if (dir.value == Direction.DOWN) {
                    dir.value = Direction.UP;
                    
                } else if (dxOld.value < 0) {
                    if (dir.value == Direction.RIGHT) {
                        dir.value = Direction.LEFT;
                    }
                    
                } else if (dxOld.value > 0) {
                    if (dir.value == Direction.LEFT) {
                        dir.value = Direction.RIGHT;
                    }
                
                } else {
                    dir.value = Direction.UP;
                }
                
            } else if (dyOld.value > 0) {
                if (dir.value == Direction.UP) {
                    dir.value = Direction.DOWN;
                    
                } else if (dxOld.value < 0) {
                    if (dir.value == Direction.RIGHT) {
                        dir.value = Direction.LEFT;
                    }
                    
                } else if (dxOld.value > 0) {
                    if (dir.value == Direction.LEFT) {
                        dir.value = Direction.RIGHT;
                    }
                    
                } else {
                    dir.value = Direction.DOWN;
                }
                
            } else {
                if (dx.value < 0) {
                    dir.value = Direction.LEFT;
                    
                } else if (dxOld.value > 0) {
                    dir.value = Direction.RIGHT;
                    
                } else {
                    // keep facing previous direction
                }
            }
        }
    }
}