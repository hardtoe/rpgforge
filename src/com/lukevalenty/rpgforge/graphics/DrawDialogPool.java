package com.lukevalenty.rpgforge.graphics;

import com.lukevalenty.rpgforge.memory.ObjectPool;

public class DrawDialogPool extends ObjectPool<DrawDialog> {
    @Override
    protected DrawDialog create() {
        return new DrawDialog(this);
    }
}
