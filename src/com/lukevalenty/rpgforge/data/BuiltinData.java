package com.lukevalenty.rpgforge.data;

import android.content.Context;
import android.graphics.Rect;

public class BuiltinData {
    public static TileSetData TILESET_A1 = null;
    public static TileSetData TILESET_A2 = null;
    public static TileSetData TILESET_A3 = null;
    public static TileSetData TILESET_A4 = null;
    public static TileSetData TILESET_A5 = null;
    public static TileSetData DOORS = null;
    public static TileSetData TILESET_B = null;
    public static TileSetData TILESET_C = null;
    public static TileSetData TILESET_D = null;
    public static TileSetData TILESET_E = null;

    private static boolean loaded = false;
    private static CharacterSetData CHARSET_1A;
    
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
                    .setPassable(false)
                    .setCompatibleWith(null);
                }
            }
            
            TILESET_A1.getTiles().get(2).setLayer(1);
            
            
            TILESET_A2 = 
                new TileSetData("TileA2.png");
            
            TILESET_A2.load(context);

            for (int y = 0; y < 4; y++) {
                for (int x = 0; x < 8; x++) {
                    AutoTileData tile = 
                        autotile(TILESET_A2, 1, x * 64, y * 96);
                }
            }
            
            TILESET_A2.getTiles().get(1).setPassable(false);
            TILESET_A2.getTiles().get(4).setPassable(false);
            TILESET_A2.getTiles().get(7).setPassable(false).setLayer(1);
            TILESET_A2.getTiles().get(12).setPassable(false);
            TILESET_A2.getTiles().get(13).setPassable(false);
            TILESET_A2.getTiles().get(15).setPassable(false).setLayer(1);
            TILESET_A2.getTiles().get(17).setPassable(false);
            TILESET_A2.getTiles().get(18).setPassable(false);
            TILESET_A2.getTiles().get(20).setPassable(false);
            TILESET_A2.getTiles().get(21).setPassable(false);
            TILESET_A2.getTiles().get(23).setPassable(false).setLayer(1);
            TILESET_A2.getTiles().get(25).setPassable(false);
            TILESET_A2.getTiles().get(28).setPassable(false);
            TILESET_A2.getTiles().get(31).setPassable(false).setLayer(1);
            
            TILESET_A2.getTiles().get(4).setLayer(1);
            TILESET_A2.getTiles().get(5).setLayer(1);
            TILESET_A2.getTiles().get(12).setLayer(1);
            
            
            
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
                    
                    if (y > 8) {
                        tile.setPassable(false);
                    }
                    
                    TILESET_A5.addTile(tile);
                }
            }
            
            // sand is compatible with beach
            ((AutoTileData) TILESET_A2.getTiles().get(3)).setCompatibleWith(TILESET_A1.getTiles().get(3));
            
            DOORS = 
                new TileSetData("doors.png");
            
            DOORS.load(context);
            
            DOORS.addTile(new BasicTileData(DOORS, new Rect(7 * 32, 0, 7 * 32 + 32, 32)).setLayer(1));
            
            DOORS.addTile(new BasicTileData(DOORS, new Rect(7 * 32, 32, 7 * 32 + 32, 32 + 32)).setLayer(1));
            
            TILESET_B = load(context, "TileB.png", 1);
            TILESET_C = load(context, "TileC.png", 1);
            TILESET_D = load(context, "TileD.png", 1);
            TILESET_E = load(context, "TileE.png", 1);
            
            
            CHARSET_1A = new CharacterSetData("vx_chara01_a.png");
            CHARSET_1A.load(context);
            
            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 2; y++) {
                    CHARSET_1A.addCharacter(new CharacterData(CHARSET_1A, new Rect(x * 32, y * 48, x * 32 + 32, y * 48 + 48)));
                }
            }
        }
    }
   
    private static TileSetData load(final Context context, final String assetName, final int layer) {
        final TileSetData tileSet = 
            new TileSetData(assetName);
        
        tileSet.load(context);
        
        int width = tileSet.bitmap().getWidth() / 32;
        int height = tileSet.bitmap().getHeight() / 32;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final BasicTileData tile =
                    new BasicTileData(tileSet, new Rect(x * 32, y * 32, x * 32 + 32, y * 32 + 32));
                
                tile.setLayer(layer);
                
                tileSet.addTile(tile);
            }
        }
        
        return tileSet;
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
        db.addTileSet(DOORS);
        db.addTileSet(TILESET_B);
        db.addTileSet(TILESET_C);
        db.addTileSet(TILESET_D);
        db.addTileSet(TILESET_E);
        db.setDefaultTile(TILESET_A5.getTiles().get(16));
        
        db.addMap(new MapData(40, 20));
        db.getMaps().get(0).setName("Home");
        db.getMaps().get(0).fill(db.getDefaultTile());
        
        db.addCharacterSet(CHARSET_1A);
        
        return db;
    }
}
