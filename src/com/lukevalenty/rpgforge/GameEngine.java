package com.lukevalenty.rpgforge;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.inject.Inject;
import com.lukevalenty.rpgforge.graphics.DrawCommand;
import com.lukevalenty.rpgforge.graphics.DrawCommandBuffer;
import com.lukevalenty.rpgforge.graphics.DrawSpritePool;

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
        private final DrawSpritePool spritePool;

        private boolean mRunning;

        private Bitmap bitmap;
        
        @Inject MainLoop(
            final DrawCommandBuffer drawCommandBuffer,
            final DrawSpritePool spritePool,
            final Context context
        ) {
            this.drawCommandBuffer = drawCommandBuffer;
            this.spritePool = spritePool;

            try {
                InputStream ims = context.getAssets().open("TileA1.png");
                bitmap = BitmapFactory.decodeStream(ims);
                
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public void run() {
            final Random r = new Random(1);
            
            while(mRunning){
                final ArrayList<DrawCommand> backBuffer = 
                    drawCommandBuffer.lockBackBuffer();
                
                r.setSeed(1);
                
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 12; y++) {
                        
                        final int srcTileX = 
                            (int) (r.nextFloat() * 16);
                        
                        final int srcTileY = 
                            (int) (r.nextFloat() * 12);
                        
                        backBuffer.add(spritePool.get().set(
                            bitmap, 
                            
                            srcTileY * 32, 
                            srcTileX * 32, 
                            srcTileX * 32 + 32, 
                            srcTileY * 32 + 32, 
                            
                            y * 48, 
                            x * 48, 
                            x * 48 + 48, 
                            y * 48 + 48));
                    }
                }
                
                drawCommandBuffer.unlockBackBuffer();
            }
        }

        public void setRunning(final boolean b) {
            mRunning = b;
        }
    }
}
