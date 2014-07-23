package com.lukevalenty.rpgforge.engine;

import com.lukevalenty.rpgforge.engine.input.GameInput;

public class PlayerControlComponent extends GameObjectComponent {
    public static final double WALK_SPEED = 0.14;

    private GameObject gameObject;
    
    private NumberRef x;
    private NumberRef y;
    private NumberRef dx;
    private NumberRef dy;
    private ObjectRef<Direction> dir;
    private BooleanRef walking;

    private ActivateMessage activateMsg;
    private WalkOverMessage walkOverMsg;

    private BooleanRef inCombat;
    
    public PlayerControlComponent() {
        // do nothing
    }

    @Override
    public void init(
        final GameObject o, 
        final GlobalGameState globalState
    ) {
        this.gameObject = o;
        this.x = o.getNumberRef("x");
        this.y = o.getNumberRef("y");
        this.dx = o.getNumberRef("dx");
        this.dy = o.getNumberRef("dy");
        this.dir = o.getObjectRef("dir");
        this.walking = o.getBooleanRef("walking");
        this.inCombat = o.getBooleanRef("inCombat");
        this.activateMsg = new ActivateMessage(o);
        this.walkOverMsg = new WalkOverMessage(o);
    }
    
    @Override
    public void update(
        final FrameState frameState,
        final GlobalGameState globalState
    ) {
        if (
            (frameState.phase == GamePhase.UPDATE) && 
            (inCombat.value == false)
        ) {
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
            
            // EVENT ACTIVATION BY POSITION
            globalState.sendMessage(walkOverMsg, (int) ((x.value + 16) / 32), (int) ((y.value + 48)/ 32));
            
            // EVENT ACTIVATION BY ACTION
            if (g.action()) {
                final int xTile = 
                    (int) (((x.value + 16) / 32) + dir.value.x);
                
                final int yTile = 
                    (int) (((y.value + 48)/ 32) + dir.value.y);
                
                globalState.sendMessage(activateMsg, xTile, yTile);
            }
        }
    }
}