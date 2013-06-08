package com.lukevalenty.rpgforge.editor.map;

public class ScaleMapEvent {
    private final float scale;
    private final float x;
    private final float y;
    
    public ScaleMapEvent(
        final float scale, 
        final float x, 
        final float y
    ) {
        this.scale = scale;
        this.x = x;
        this.y = y;
    }
    
    public float scale() {
        return scale;
    }
    
    public float x() {
        return x;
    }
    
    public float y() {
        return y;
    }
}
