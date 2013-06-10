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
                    frameState.drawBuffer.add(frameState.tilemapPool.get().set(globalState.getMap()).setLower().setZ(-1000));
                    frameState.drawBuffer.add(frameState.tilemapPool.get().set(globalState.getMap()).setUpper().setZ(32 * 1000));
                }
            }
        });
    }
}
