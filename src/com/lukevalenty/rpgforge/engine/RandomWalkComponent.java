package com.lukevalenty.rpgforge.engine;

public class RandomWalkComponent extends GameObjectComponent {
    // shared object state
    private final NumberRef dx;
    private final NumberRef dy;
    private final ObjectRef<Direction> dir;
    private final BooleanRef walking;
    
    // local state
    private float decisionTimeframe = 5000;
    private float timeSinceLastDecision = Float.POSITIVE_INFINITY;
    
    public RandomWalkComponent(
        final GameObject o
    ) {
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
            final float timeDelta =
                frameState.timeDelta;
            
            timeSinceLastDecision += timeDelta;
            
            if (timeSinceLastDecision > decisionTimeframe) {
                walking.value = (Math.random() > 0.7); 
                dir.value = Direction.values()[(int) (Direction.values().length * Math.random())];
                timeSinceLastDecision = 0;
            }
            
            if (walking.value) {
                decisionTimeframe = 1000;
                dx.value = PlayerControlComponent.WALK_SPEED *.5 * timeDelta * dir.value.x;
                dy.value = PlayerControlComponent.WALK_SPEED *.5 * timeDelta * dir.value.y;
                
            } else {
                decisionTimeframe = 5000;
                dx.value = 0;
                dy.value = 0;
            }
                        
        }
    }
}