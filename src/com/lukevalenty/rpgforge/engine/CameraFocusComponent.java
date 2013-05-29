package com.lukevalenty.rpgforge.engine;

public class CameraFocusComponent extends GameObjectComponent {
    private final NumberRef x;
    private final NumberRef y;

    public CameraFocusComponent(final GameObject o) {
        this.x = o.getNumberRef("x");
        this.y = o.getNumberRef("y");
    }

    @Override
    public void update(
        final FrameState frameState,
        final GlobalGameState globalState
    ) {
        if (frameState.phase == GamePhase.MOVE) {
            globalState.setFocus((int) x.value + 16, (int) y.value + 32);
        }
    }
}