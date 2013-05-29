package com.lukevalenty.rpgforge.edit;

public class PaletteItemSelectedEvent {
    private final PaletteItem tile;
    
    public PaletteItemSelectedEvent(
        final PaletteItem tile
    ) {
        this.tile = tile;
    }
    
    public PaletteItem tile() {
        return tile;
    }
}
