package com.lukevalenty.rpgforge.edit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.Log;

import com.google.inject.Inject;
import com.lukevalenty.rpgforge.RpgForgeApplication;
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
    private final static String TAG = "MapEditEngine";
    
    private final MainLoop mainLoop;
    private Thread mainLoopThread;

    private final EventBus eventBus;

    @Inject MapEditEngine(
        final MainLoop mainLoop,
        final EventBus eventBus
    ) {
        this.mainLoop = mainLoop;
        this.eventBus = eventBus;
    }
    
    public void start() {
        if (mainLoop.mRunning == false) {
            mainLoopThread = new Thread(mainLoop);
            mainLoop.setRunning(true);
            mainLoopThread.start();
            
            eventBus.register(mainLoop);
        }
    }
    
    public void stop() {
        if (mainLoop.mRunning) {
            eventBus.unregister(mainLoop);
            
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
    }

    public static class MainLoop implements Runnable {
        private static final String TAG = MainLoop.class.getCanonicalName();
        
        private final DrawCommandBuffer drawCommandBuffer;
        private final SetMatrixPool setMatrixPool;
        private final DrawSpritePool spritePool;
        private final DrawTileMapPool tilemapPool;

        private boolean mRunning;

        private MapData currentMap;
        private Bitmap currentMapBitmap;

        private Matrix viewMatrix = new Matrix();
        private Matrix inverseViewMatrix = new Matrix();

        private EventBus eventBus;
        
        @Inject MainLoop(
            final DrawCommandBuffer drawCommandBuffer,
            final SetMatrixPool setMatrixPool,
            final DrawSpritePool spritePool,
            final DrawTileMapPool tilemapPool,
            final Context context,
            final EventBus eventBus
        ) {
            this.drawCommandBuffer = drawCommandBuffer;
            this.setMatrixPool = setMatrixPool;
            this.spritePool = spritePool;
            this.tilemapPool = tilemapPool;
            this.eventBus = eventBus;
            
            BuiltinData.load(context);

            viewMatrix.postScale(2, 2);
        }

        public void onEvent(
            final SelectMapEvent e
        ) {
            currentMap = e.map();
            currentMapBitmap = Bitmap.createBitmap(currentMap.getWidth(), currentMap.getHeight(), Bitmap.Config.ARGB_8888);
            
            updateMapBitmap();
        }

        private void updateMapBitmap() {
            for (int y = 0; y < currentMap.getHeight(); y++) {
                for (int x = 0; x < currentMap.getWidth(); x++) {
                    final TileData sparseTile = 
                        currentMap.getSparseTile(x, y);
                    
                    if (sparseTile == null) {
                        currentMapBitmap.setPixel(x, y, currentMap.getTile(x, y).getAvgColor());
                    } else {
                        currentMapBitmap.setPixel(x, y, sparseTile.getAvgColor());
                    }
                }
            }
        }
        
        public void onEvent(
            final EyedropEvent e
        ) {
            viewMatrix.invert(inverseViewMatrix);
            
            pts[0] = e.x();
            pts[1] = e.y();
            inverseViewMatrix.mapPoints(pts);
            float x = pts[0];
            float y = pts[1];
            
            int tileX = (int) (x / 32);
            int tileY = (int) (y / 32);
            
            eventBus.post(new TileSelectedEvent(currentMap.getTile(tileX, tileY)));
        }

        private float[] pts = new float[2];

        private boolean fillInProgress;
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
            currentMapBitmap.setPixel(tileX, tileY, e.tile().getAvgColor());
        }
        
        public void onEvent(
            final ResizeMapEvent e
        ) {
            currentMap.resize(e.width(), e.height(), RpgForgeApplication.getDb().getDefaultTile());
            currentMapBitmap = Bitmap.createBitmap(currentMap.getWidth(), currentMap.getHeight(), Bitmap.Config.ARGB_8888);
            updateMapBitmap();
        }
        
        public void onEvent(
            final FillTileEvent e
        ) {
            if (fillInProgress == false) {
                viewMatrix.invert(inverseViewMatrix);
                
                pts[0] = e.x();
                pts[1] = e.y();
                inverseViewMatrix.mapPoints(pts);
                float x = pts[0];
                float y = pts[1];
                
                final int tileX = (int) (x / 32);
                final int tileY = (int) (y / 32);
               
                fillInProgress = true;
                
                new Thread(new Runnable() {
                    
                    @Override
                    public void run() {
                        fill(e.tile(), tileX, tileY);
                        fillInProgress = false;
                    }
                }).start();
            }
        }
        
        
        /**
         * Algorithm from: http://en.wikipedia.org/wiki/Flood_fill
         * @param replacementTile
         * @param x
         * @param y
         */
        private void fill(
            final TileData replacementTile, 
            final int x, 
            final int y
        ) {
            if (replacementTile.getLayer() == 0) {
                final int color = 
                    replacementTile.getAvgColor();
                
                final TileData targetTile = 
                    currentMap.getTile(x, y);
                
                if (targetTile != null && !replacementTile.equals(targetTile)) { 
                    LinkedHashSet<Point> q = 
                        new LinkedHashSet<Point>();
                    
                    q.add(new Point(x, y));
                    
                    while (!q.isEmpty()) {
                        final Iterator<Point> i = q.iterator();
                        
                        final Point n = 
                            i.next();

                        i.remove();
                        
                        final TileData nTile = 
                                currentMap.getTile(n.x, n.y);
                        
                        if (nTile != null && nTile == targetTile) {
                            currentMap.setTile(n.x, n.y, replacementTile);
                            currentMapBitmap.setPixel(n.x, n.y, color);
                            addPoint(targetTile, q, new Point(n.x + 1, n.y));
                            addPoint(targetTile, q, new Point(n.x - 1, n.y));
                            addPoint(targetTile, q, new Point(n.x, n.y + 1));
                            addPoint(targetTile, q, new Point(n.x, n.y - 1));
                        }
                    }
                    
                    q = null;
                }
            }
        }


        private void addPoint(
            final TileData targetTile, 
            final LinkedHashSet<Point> q, 
            final Point p
        ) {
            final TileData tile = 
                currentMap.getTile(p.x, p.y);
            
            if (tile != null && tile.equals(targetTile)) {
                q.add(p);
            }
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
                backBuffer.add(tilemapPool.get().set(currentMap, currentMapBitmap));
                
                drawCommandBuffer.unlockBackBuffer();
            }
        }

        public void setRunning(final boolean b) {
            mRunning = b;
        }
    }
}
