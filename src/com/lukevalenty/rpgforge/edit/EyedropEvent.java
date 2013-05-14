package com.lukevalenty.rpgforge.edit;

import java.util.List;

import com.lukevalenty.rpgforge.data.TileData;

public class EyedropEvent {
    private final int x;
    private final int y;
    
    public EyedropEvent(
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
