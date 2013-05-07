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
    public static TileSetData TILESET_A3 = null;
    public static TileSetData TILESET_A4 = null;
    public static TileSetData TILESET_A5 = null;

    private static boolean loaded = false;
    
    static public void load(final Context context) {
        if (!loaded) {
            loaded = true;
            

            
            
            TILESET_A1 = 
                new TileSetData("TileA1.png");
    
            TILESET_A1.load(context);

            for (int y = 0; y < 4; y++) {
                for (int x = 0; x < 2; x++) {
                    autotile(TILESET_A1, 3, x * 256, y * 96)
                    .setFrameDelay(32)
                    .setAnimationSequence(0, 1, 2, 1)
                    .setPassable(false);
                }
            }
            
            
            
            TILESET_A2 = 
                new TileSetData("TileA2.png");
            
            TILESET_A2.load(context);

            for (int y = 0; y < 4; y++) {
                for (int x = 0; x < 8; x++) {
                    autotile(TILESET_A2, 1, x * 64, y * 96);
                }
            }
            
            
            
            TILESET_A3 = 
                new TileSetData("TileA3.png");
            
            TILESET_A3.load(context);

            for (int y = 0; y < 4; y++) {
                for (int x = 0; x < 8; x++) {
                    autotile(TILESET_A3, 1, x * 64, y * 64)
                    .setPassable(false)
                    .setConcaveCorners(false);
                }
            }
            
            
            
            TILESET_A4 = 
                new TileSetData("TileA4.png");
            
            TILESET_A4.load(context);

            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 8; x++) {
                    autotile(TILESET_A4, 1, x * 64, y * 160);
                    
                    autotile(TILESET_A4, 1, x * 64, y * 160 + 96)
                    .setPassable(false)
                    .setConcaveCorners(false);
                }
            }
            
            
            
            TILESET_A5 = 
                    new TileSetData("TileA5.png");
            
            TILESET_A5.load(context);

            for (int y = 0; y < 16; y++) {
                for (int x = 0; x < 8; x++) {
                    final BasicTileData tile = 
                        new BasicTileData(TILESET_A5, new Rect(x * 32, y * 32, x * 32 + 32, y * 32 + 32));
                    
                    TILESET_A5.addTile(tile);
                }
            }
    
            MAP = new MapData(40, 40);
            
            for (int x = 0; x < 40; x++) {
                for (int y = 0; y < 40; y++) {
                    MAP.setTile(x, y, TILESET_A5.getTiles().get(16));
                }
            }
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
