package com.lukevalenty.rpgforge.editor.map;

public class DrawTileEvent {
    private final PaletteItem tile;
    private final int x;
    private final int y;
    
    public DrawTileEvent(
        final PaletteItem tile,
        final int x,
        final int y
    ) {
        this.tile = tile;
        this.x = x;
        this.y = y;
    }

    public PaletteItem tile() {
        return tile;
    }
    
    public int x() {
        return x;
    }
    
    public int y() {
        return y;
    }
}
