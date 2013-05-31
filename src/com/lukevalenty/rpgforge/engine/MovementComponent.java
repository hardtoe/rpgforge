package com.lukevalenty.rpgforge.engine;

public class MovementComponent extends GameObjectComponent {
    private NumberRef dx;
    private NumberRef dy;
    private NumberRef x;
    private NumberRef y;

    public MovementComponent() {
        // do nothing
    }
    
    public MovementComponent(final GameObject o) {
        this.dx = o.getNumberRef("dx");
        this.dy = o.getNumberRef("dy");
        this.x = o.getNumberRef("x");
        this.y = o.getNumberRef("y");
    }

    @Override
    public void update(
        final FrameState frameState,
        final GlobalGameState globalState
    ) {
        if (frameState.phase == GamePhase.MOVE) {
            x.value = Math.max(0, x.value + dx.value);
            y.value = Math.max(-16, y.value + dy.value);
        }
    }
}