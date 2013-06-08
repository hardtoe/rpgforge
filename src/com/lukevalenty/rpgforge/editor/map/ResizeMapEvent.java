package com.lukevalenty.rpgforge.editor.map;

public class ResizeMapEvent {
    private final int width;
    private final int height;

    public ResizeMapEvent(
        final int width, 
        final int height
    ) {
        this.width = width;
        this.height = height;
    }

    public int width() {
        return width;
    }
    
    public int height() {
        return height;
    }
}
