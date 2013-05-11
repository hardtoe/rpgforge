package com.lukevalenty.rpgforge.edit;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Matrix;
import android.util.Log;

import com.google.inject.Inject;
import com.lukevalenty.rpgforge.data.BuiltinData;
import com.lukevalenty.rpgforge.data.MapData;
import com.lukevalenty.rpgforge.data.TileData;
import com.lukevalenty.rpgforge.graphics.DrawCommand;
import com.lukevalenty.rpgforge.graphics.DrawCommandBuffer;
import com.lukevalenty.rpgforge.graphics.DrawSpritePool;
import com.lukevalenty.rpgforge.graphics.DrawTileMapPool;
import com.lukevalenty.rpgforge.graphics.SetMatrixPool;

import de.greenrobot.event.EventBus;

public class MapEditEngine {
    private final MainLoop mainLoop;
    private Thread mainLoopThread;

    @Inject MapEditEngine(
        final MainLoop mainLoop
    ) {
        this.mainLoop = mainLoop;
    }
    
    public void setMap(final MapData map) {
        mainLoop.setMap(map);
    }
    
    public void start() {
        mainLoopThread = new Thread(mainLoop);
        mainLoop.setRunning(true);
        mainLoopThread.start();
    }
    
    public void stop() {
        mainLoop.setRunning(false);
        
        if (mainLoopThread != null) {
            try {
                mainLoopThread.join();
                mainLoopThread = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class MainLoop implements Runnable {
        private static final String TAG = MainLoop.class.getCanonicalName();
        
        private final DrawCommandBuffer drawCommandBuffer;
        private final SetMatrixPool setMatrixPool;
        private final DrawSpritePool spritePool;
        private final DrawTileMapPool tilemapPool;
        private final EventBus eventBus;

        private boolean mRunning;

        private MapData currentMap;
        

        private Matrix viewMatrix = new Matrix();
        private Matrix inverseViewMatrix = new Matrix();
        
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
            
            eventBus.register(this, PanMapEvent.class, ScaleMapEvent.class, DrawTileEvent.class, FillTileEvent.class);
            
            BuiltinData.load(context);

            viewMatrix.postScale(2, 2);
        }

        
        public void setMap(final MapData map) {
            this.currentMap = map;
        }


        private float[] pts = new float[2];
        public void onEvent(
            final DrawTileEvent e
        ) {
            viewMatrix.invert(inverseViewMatrix);
            
            pts[0] = e.x();
            pts[1] = e.y();
            inverseViewMatrix.mapPoints(pts);
            float x = pts[0];
            float y = pts[1];
            
            int tileX = (int) (x / 32);
            int tileY = (int) (y / 32);
            
            currentMap.setTile(tileX, tileY, e.tile());
        }
        
        public void onEvent(
            final FillTileEvent e
        ) {
            viewMatrix.invert(inverseViewMatrix);
            
            pts[0] = e.x();
            pts[1] = e.y();
            inverseViewMatrix.mapPoints(pts);
            float x = pts[0];
            float y = pts[1];
            
            int tileX = (int) (x / 32);
            int tileY = (int) (y / 32);
            
            currentMap.fill(e.tile(), tileX, tileY);
        }
        
        public void onEvent(
            final PanMapEvent e
        ) {   
            viewMatrix.postTranslate(e.x(), e.y());
        }
        
        public void onEvent(
            final ScaleMapEvent e        
        ) {
            viewMatrix.postScale(e.scale(), e.scale(), e.x(), e.y());
        }

        
        @Override
        public void run() {
            while(mRunning){
                final ArrayList<DrawCommand> backBuffer = 
                    drawCommandBuffer.lockBackBuffer();
                
                backBuffer.add(setMatrixPool.get().set(viewMatrix));
                backBuffer.add(tilemapPool.get().set(currentMap));
                
                drawCommandBuffer.unlockBackBuffer();
            }
        }

        public void setRunning(final boolean b) {
            mRunning = b;
        }
    }
}
