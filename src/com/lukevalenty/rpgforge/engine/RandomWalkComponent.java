package com.lukevalenty.rpgforge.engine;

public class RandomWalkComponent extends GameObjectComponent {
    // shared object state
    private NumberRef dx;
    private NumberRef dy;
    private ObjectRef<Direction> dir;
    private BooleanRef walking;
    private BooleanRef stationary;

    private transient BooleanRef inCombat;
    
    // local state
    private float decisionTimeframe = 5000;
    private float timeSinceLastDecision = Float.POSITIVE_INFINITY;
    
    public RandomWalkComponent() {
        // do nothing
    }

    @Override
    public void init(
        final GameObject o, 
        final GlobalGameState globalState
    ) {
        this.dx = o.getNumberRef("dx");
        this.dy = o.getNumberRef("dy");
        this.dir = o.getObjectRef("dir");
        this.walking = o.getBooleanRef("walking");
        this.stationary = o.getBooleanRef("stationary");
        this.inCombat = o.getBooleanRef("inCombat");
    }

    @Override
    public void update(
        final FrameState frameState,
        final GlobalGameState globalState
    ) {
        if (stationary == null || !stationary.value) {
            if (frameState.phase == GamePhase.UPDATE) {
                if (inCombat.value) {
                    dx.value = 0;
                    dy.value = 0;
                    
                } else {
                    if (dir.value == null) {
                        dir.value = Direction.DOWN;
                    }
                    
                    final float timeDelta =
                        frameState.timeDelta;
                    
                    timeSinceLastDecision += timeDelta;
                    
                    if (timeSinceLastDecision > decisionTimeframe) {
                        walking.value = (Math.random() > 0.7); 
                        dir.value = Direction.getDirection((int) (4 * Math.random()));
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
    }
}