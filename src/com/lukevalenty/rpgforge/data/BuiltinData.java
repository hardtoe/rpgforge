package com.lukevalenty.rpgforge.data;

import android.content.Context;
import android.graphics.Rect;

public class BuiltinData {
    public static TileSetData TILESET_A1 = null;
    public static TileSetData TILESET_A2 = null;
    public static TileSetData TILESET_A3 = null;
    public static TileSetData TILESET_A4 = null;
    public static TileSetData TILESET_A5 = null;

    private static boolean loaded = false;
    
    public static void load(final Context context) {
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


    public static RpgDatabase createNewDatabase(final Context context) {
        load(context);
        
        RpgDatabase db = 
            new RpgDatabase();
        
        db.addTileSet(TILESET_A1);
        db.addTileSet(TILESET_A2);
        db.addTileSet(TILESET_A3);
        db.addTileSet(TILESET_A4);
        db.addTileSet(TILESET_A5);
        db.setDefaultTile(TILESET_A5.getTiles().get(16));
        
        db.addMap(new MapData(40, 20));
        db.getMaps().get(0).setName("Home");
        db.getMaps().get(0).fill(db.getDefaultTile());
        
        return db;
    }
}
