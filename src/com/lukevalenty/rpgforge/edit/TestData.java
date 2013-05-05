package com.lukevalenty.rpgforge.edit;

import android.content.Context;
import android.graphics.Rect;

import com.lukevalenty.rpgforge.data.AutoTileData;
import com.lukevalenty.rpgforge.data.BasicTileData;
import com.lukevalenty.rpgforge.data.MapData;
import com.lukevalenty.rpgforge.data.TileSetData;

public class TestData {
    public static MapData MAP = null;
    public static TileSetData TILESET_A1 = null;
    public static TileSetData TILESET_A2 = null;
    public static TileSetData TILESET_A5 = null;

    private static boolean loaded = false;
    
    static public void load(final Context context) {
        if (!loaded) {
            loaded = true;
            
            TILESET_A5 = 
                new TileSetData("TileA5.png");
            
            TILESET_A5.load(context);
    
            
            BasicTileData blank = 
                new BasicTileData(TILESET_A5, new Rect(0, 0, 32, 32));
            
            BasicTileData sand = 
                new BasicTileData(TILESET_A5, new Rect(32, 64, 64, 96));
            
            
            TILESET_A1 = 
                new TileSetData("TileA1.png");
    
            TILESET_A1.load(context);
            
            
            TILESET_A2 = 
                new TileSetData("TileA2.png");
            
            TILESET_A2.load(context);
    
            AutoTileData w = // water
                autotile(TILESET_A1, 3, 0, 0)
                .setFrameDelay(32)
                .setAnimationSequence(0, 1, 2, 1)
                .setPassable(false);

            AutoTileData W = // other water
                autotile(TILESET_A1, 3, 256, 0)
                .setFrameDelay(32)
                .setAnimationSequence(0, 1, 2, 1)
                .setPassable(false);

            AutoTileData y = // dirt water
                autotile(TILESET_A1, 3, 0, 192)
                .setFrameDelay(32)
                .setAnimationSequence(0, 1, 2, 1)
                .setPassable(false);

            AutoTileData c = // cave water
                autotile(TILESET_A1, 3, 0, 288)
                .setFrameDelay(32)
                .setAnimationSequence(0, 1, 2, 1)
                .setPassable(false);

            AutoTileData t = // temple water
                autotile(TILESET_A1, 3, 256, 192)
                .setFrameDelay(32)
                .setAnimationSequence(0, 1, 2, 1)
                .setPassable(false);

            AutoTileData L = // lava
                autotile(TILESET_A1, 3, 256, 288)
                .setFrameDelay(32)
                .setAnimationSequence(0, 1, 2, 1)
                .setPassable(false);
    
            AutoTileData g = // grass
                autotile(TILESET_A2, 1, 0, 0)
                .setPassable(true);
    
            AutoTileData d = // dark grass
                autotile(TILESET_A2, 1, 0, 192)
                .setPassable(true);
    
            AutoTileData G = // thick grass
                autotile(TILESET_A2, 1, 64, 96)
                .setPassable(true);
    
            AutoTileData k = // grassy knoll
                autotile(TILESET_A2, 1, 64, 192)
                .setPassable(false);
    
            AutoTileData h = // hole
                autotile(TILESET_A2, 1, 128, 192)
                .setPassable(true);
    
            // dirty grass
            autotile(TILESET_A2, 1, 128, 96)
            .setPassable(true);
    
            // grassy dirt
            autotile(TILESET_A2, 1, 192, 288)
            .setPassable(true);
    
            // dirt hole
            autotile(TILESET_A2, 1, 256, 288)
            .setPassable(true);
    
            // stony dirt
            autotile(TILESET_A2, 1, 256 + 64, 288)
            .setPassable(true);
    
            AutoTileData F = // fence
                autotile(TILESET_A2, 1, 64, 0)
                .setPassable(false);
    
            AutoTileData P = // picket fence
                autotile(TILESET_A2, 1, 256, 0)
                .setPassable(false);
    
            AutoTileData S = // stone fence
                autotile(TILESET_A2, 1, 256, 96)
                .setPassable(false);
    
            AutoTileData p = // cobblestone path
                autotile(TILESET_A2, 1, 128, 0)
                .setCompatibleWith(null)
                .setPassable(true);
            
            AutoTileData b = // brick path
                autotile(TILESET_A2, 1, 192, 96)
                .setCompatibleWith(null)
                .setCompatibleWith(t)
                .setPassable(true);
    
            AutoTileData f = // flowers
                autotile(TILESET_A2, 1, 0, 96)
                .setPassable(true);
    
    
            AutoTileData o = // ocean
                autotile(TILESET_A1, 3, 256, 96)
                .setCompatibleWith(null)
                .setFrameDelay(32)
                .setAnimationSequence(0, 1, 2, 1)
                .setPassable(false);
            
            AutoTileData s = // sand
                autotile(TILESET_A2, 1, 192, 0)
                .setCompatibleWith(o)
                .setCompatibleWith(null)
                .setPassable(true);
            
            MAP = 
                new MapData(40, 40,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, 
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, 
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, 
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, 
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, 
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, 
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, 
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, 
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g
                );
        }
    }

    
    private static AutoTileData autotile(
        final TileSetData tileset,
        final int frames,
        final int left,
        final int top
    ) {
        final Rect[] bitmaps =
            new Rect[frames];
        
        for (int i = 0; i < frames; i++) {
            bitmaps[i] = new Rect(left + (64 * i), top, left + 64, top + 96);
        }
        
        final AutoTileData autoTileData = 
            new AutoTileData(tileset, bitmaps);
        
        tileset.addTile(autoTileData);
        
        return autoTileData;
    }
}
