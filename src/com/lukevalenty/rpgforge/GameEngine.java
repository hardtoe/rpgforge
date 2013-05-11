package com.lukevalenty.rpgforge;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;

import com.google.inject.Inject;
import com.lukevalenty.rpgforge.data.AutoTileData;
import com.lukevalenty.rpgforge.data.BasicTileData;
import com.lukevalenty.rpgforge.data.MapData;
import com.lukevalenty.rpgforge.data.TileData;
import com.lukevalenty.rpgforge.data.TileSetData;
import com.lukevalenty.rpgforge.graphics.DrawCommand;
import com.lukevalenty.rpgforge.graphics.DrawCommandBuffer;
import com.lukevalenty.rpgforge.graphics.DrawSpritePool;
import com.lukevalenty.rpgforge.graphics.DrawTileMapPool;
import com.lukevalenty.rpgforge.graphics.SetMatrixPool;

import de.greenrobot.event.EventBus;

public class GameEngine {
    private final MainLoop mainLoop;
    private Thread mainLoopThread;

    @Inject GameEngine(
        final MainLoop mainLoop
    ) {
        this.mainLoop = mainLoop;
    }

    public void start() {
        mainLoopThread = new Thread(mainLoop);
        mainLoop.setRunning(true);
        mainLoopThread.start();
    }
    
    public void stop() {
        mainLoop.setRunning(false);
        
        try {
            mainLoopThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static class MainLoop implements Runnable {
        private final DrawCommandBuffer drawCommandBuffer;
        private final SetMatrixPool setMatrixPool;
        private final DrawSpritePool spritePool;
        private final DrawTileMapPool tilemapPool;
        private final EventBus eventBus;

        private boolean mRunning;

        private MapData map;
        
        @Inject MainLoop(
            final DrawCommandBuffer drawCommandBuffer,
            final SetMatrixPool setMatrixPool,
            final DrawSpritePool spritePool,
            final DrawTileMapPool tilemapPool,
            final EventBus eventBus,
            final Context context
        ) {
            this.drawCommandBuffer = drawCommandBuffer;
            this.setMatrixPool = setMatrixPool;
            this.spritePool = spritePool;
            this.tilemapPool = tilemapPool;
            this.eventBus = eventBus;
            

            TileSetData tileSetA5 = 
                new TileSetData("TileA5.png");
            
            tileSetA5.load(context);

            
            BasicTileData blank = 
                new BasicTileData(tileSetA5, new Rect(0, 0, 32, 32));
            
            BasicTileData sand = 
                new BasicTileData(tileSetA5, new Rect(32, 64, 64, 96));
            
            
            TileSetData tileSetA1 = 
                new TileSetData("TileA1.png");

            tileSetA1.load(context);
            
            
            TileSetData tileSetA2 = 
                new TileSetData("TileA2.png");
            
            tileSetA2.load(context);

            AutoTileData w = // water
                autotile(tileSetA1, 3, 0, 0)
                .setFrameDelay(32)
                .setAnimationSequence(0, 1, 2, 1)
                .setPassable(false);

            AutoTileData g = // grass
                autotile(tileSetA2, 1, 0, 0)
                .setPassable(true);

            AutoTileData G = // thick grass
                autotile(tileSetA2, 1, 64, 96)
                .setPassable(true);

            AutoTileData k = // grassy knoll
                autotile(tileSetA2, 1, 64, 192)
                .setPassable(false);

            AutoTileData F = // fence
                autotile(tileSetA2, 1, 64, 0)
                .setPassable(false);

            AutoTileData p = // cobblestone path
                autotile(tileSetA2, 1, 128, 0)
                .setCompatibleWith(null)
                .setPassable(true);

            AutoTileData f = // flowers
                autotile(tileSetA2, 1, 0, 96)
                .setPassable(true);


            AutoTileData o = // ocean
                autotile(tileSetA1, 3, 256, 96)
                .setCompatibleWith(null)
                .setFrameDelay(32)
                .setAnimationSequence(0, 1, 2, 1)
                .setPassable(false);
            
            AutoTileData s = // sand
                autotile(tileSetA2, 1, 192, 0)
                .setCompatibleWith(o)
                .setCompatibleWith(null)
                .setPassable(true);
            
            map = 
                new MapData(20, 10,
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, s, s, o, o, o, 
                    g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, g, s, s, o, o, 
                    g, g, F, F, F, F, F, F, g, k, k, g, G, G, G, G, G, s, o, o, 
                    g, g, F, w, w, w, w, F, g, g, k, g, G, G, G, G, G, s, o, o, 
                    g, g, F, w, w, w, w, F, g, g, k, g, G, G, G, G, G, s, o, o, 
                    g, g, F, w, w, w, w, F, g, k, k, k, G, G, G, G, G, s, o, o, 
                    g, g, F, F, p, p, F, F, g, g, k, g, g, g, g, g, g, s, o, o, 
                    g, g, g, f, p, p, f, g, g, g, g, g, g, g, g, g, g, s, o, o, 
                    g, g, g, f, p, p, p, p, p, p, p, p, p, p, p, p, p, s, o, o, 
                    g, g, g, f, p, p, f, g, g, g, g, g, g, g, g, g, s, s, o, o
                );

        }

        private AutoTileData autotile(
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
            
            return autoTileData;
        }
        
        private Matrix matrix = new Matrix();
        
        @Override
        public void run() {
            
            while(mRunning){
                final ArrayList<DrawCommand> backBuffer = 
                    drawCommandBuffer.lockBackBuffer();

                backBuffer.add(setMatrixPool.get().set(matrix));
                backBuffer.add(tilemapPool.get().set(map));
                
                drawCommandBuffer.unlockBackBuffer();
            }
        }

        public void setRunning(final boolean b) {
            mRunning = b;
        }
    }
}
