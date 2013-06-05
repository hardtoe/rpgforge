package com.lukevalenty.rpgforge.graphics;

import com.lukevalenty.rpgforge.memory.ObjectPool;

public class DrawDialog extends DrawCommand<DrawDialog> {
    public DrawDialog(final ObjectPool<DrawDialog> objectPool) {
        super(objectPool);
    }
    
    private String[] text;
    
    public DrawDialog set(
        final String... text
    ) {
        super.init();
        
        this.text = text;
        
        return this;
    }
    
    public String[] text() {
        return text;
    }
}
