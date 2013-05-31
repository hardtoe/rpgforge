package com.lukevalenty.rpgforge.engine;

public class MapObject extends GameObject {
    public MapObject() {
        addComponent(new GameObjectComponent() {
            @Override
            public void update(
                final FrameState frameState,
                final GlobalGameState globalState
            ) {
                if (frameState.phase == GamePhase.RENDER) {
                    frameState.drawBuffer.add(frameState.tilemapPool.get().set(globalState.getMap()).setZ(-1000));
                }
            }
        });
    }
}
