package com.lukevalenty.rpgforge.engine;

public class MovementComponent extends GameObjectComponent {
    private final NumberRef dx;
    private final NumberRef dy;
    private final NumberRef x;
    private final NumberRef y;

    public MovementComponent(final GameObject o) {
        this.dx = o.getNumberRef("dx");
        this.dy = o.getNumberRef("dy");
        this.x = o.getNumberRef("x");
        this.y = o.getNumberRef("y");
    }

    @Override
    public void update(
        final FrameState frameState
    ) {
        if (frameState.phase == GamePhase.MOVE) {
            x.value = Math.max(0, x.value + dx.value);
            y.value = Math.max(-16, y.value + dy.value);
            
            frameState.globalState.setFocus((int) x.value + 16, (int) y.value + 32);
        }
    }
}