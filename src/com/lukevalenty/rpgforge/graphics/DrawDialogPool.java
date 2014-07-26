package com.lukevalenty.rpgforge.graphics;

import com.lukevalenty.rpgforge.memory.ObjectPool;

public class DrawDialogPool extends ObjectPool<DrawInGameUiWindow> {
    @Override
    protected DrawInGameUiWindow create() {
        return new DrawInGameUiWindow(this);
    }
}
