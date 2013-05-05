package com.lukevalenty.rpgforge.edit;

public class PanMapEvent {
    private final int x;
    private final int y;

    public PanMapEvent(
        final int x,
        final int y
    ) {
        this.x = x;
        this.y = y;
    }
    
    public int x() {
        return x;
    }
    
    public int y() {
        return y;
    }
}
