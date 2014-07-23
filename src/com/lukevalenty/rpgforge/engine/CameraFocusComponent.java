package com.lukevalenty.rpgforge.engine;

public class CameraFocusComponent extends GameObjectComponent {
    private NumberRef x;
    private NumberRef y;

    public CameraFocusComponent() {
        // do nothing
    }

    @Override
    public void init(
        final GameObject o, 
        final GlobalGameState globalState
    ) {
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