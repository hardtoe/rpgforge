package com.lukevalenty.rpgforge.editor.map;

import java.util.ArrayList;
import java.util.Formatter.BigDecimalLayoutForm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.google.inject.Inject;
import com.lukevalenty.rpgforge.data.BuiltinData;
import com.lukevalenty.rpgforge.data.MapData;
import com.lukevalenty.rpgforge.data.TileData;
import com.lukevalenty.rpgforge.engine.GameView;
import com.lukevalenty.rpgforge.graphics.DrawCommand;
import com.lukevalenty.rpgforge.graphics.DrawCommandBuffer;
import com.lukevalenty.rpgforge.graphics.DrawSpritePool;
import com.lukevalenty.rpgforge.graphics.DrawTileMapPool;
import com.lukevalenty.rpgforge.graphics.SetMatrixPool;


public class MapView extends GameView {
    public interface OnTileClickListener {
        public boolean onTileClick(final int tileX, final int tileY);
    }

    public class ScaleMapGestureListener 
    extends
        ScaleGestureDetector.SimpleOnScaleGestureListener 
    {
        private float lastFocusX;
        private float lastFocusY;
        
        @Override
        public boolean onScaleBegin(
            final ScaleGestureDetector scaleGestureDetector
        ) {
            lastFocusX = scaleGestureDetector.getFocusX();
            lastFocusY = scaleGestureDetector.getFocusY();
            
            return true;
        }
    
        @Override
        public boolean onScale(
            final ScaleGestureDetector scaleGestureDetector
        ) {
            final float scaleFactor = 
                scaleGestureDetector.getScaleFactor();
            
            final float focusX = 
                scaleGestureDetector.getFocusX();
           
            final float focusY = 
                scaleGestureDetector.getFocusY();
    
            if (!freezeMapLocation) {
                mapEditEngine.mainLoop.scaleMap(scaleFactor, scaleFactor, focusX, focusY);
                mapEditEngine.mainLoop.panMap(focusX - lastFocusX, focusY - lastFocusY);
            }
            
            lastFocusX = focusX;
            lastFocusY = focusY;
            
            return true;
        }
    }
    

    @SuppressLint("NewApi")
    public class MapGestureDetector 
    implements 
        OnTouchListener, 
        OnGenericMotionListener 
    {
        private final ScaleGestureDetector mScaleDetector;
        
        private float previousX;
        private float previousY;
        private int pointerId = -1;
        
        private float startX;
        private float startY;
        
        public MapGestureDetector(
            final ScaleGestureDetector mScaleDetector
        ) {
            this.mScaleDetector = mScaleDetector;
        }
        
        @SuppressLint("NewApi")
        @Override
        public boolean onTouch(
            final View v, 
            final MotionEvent e
        ) {
            mScaleDetector.onTouchEvent(e);
            
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                previousX = e.getX();
                previousY = e.getY();
                pointerId = e.getPointerId(0);
                
                startX = e.getX();
                startY = e.getY();
                
                return true;
                
            } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
                float x = e.getX();
                float y = e.getY();            
                
                float dx = x - previousX;
                float dy = y - previousY;
                
                if (
                    pointerId == e.getPointerId(0) && 
                    e.getPointerCount() == 1
                    
                ) {
                    if (
                        !freezeMapLocation ||
                        ((e.getButtonState() & MotionEvent.BUTTON_SECONDARY) != 0)
                    ) {
                        mapEditEngine.mainLoop.panMap(dx, dy);
                        
                    } else {
                        final Point p = 
                            mapEditEngine.getTileCoordinates(e.getX(), e.getY());
                        
                        if (onTileClickListener != null) {
                            onTileClickListener.onTileClick(p.x, p.y);
                        }                    
                    }
                }
                
                previousX = x;
                previousY = y;
                pointerId = e.getPointerId(0);
                
                return true;
            
            } else if (e.getAction() == MotionEvent.ACTION_UP) {
                if (
                    Math.abs(startX - e.getX()) < 100 &&
                    Math.abs(startY - e.getY()) < 100
                ) {
                    final Point p = 
                        mapEditEngine.getTileCoordinates(e.getX(), e.getY());
                    
                    if (onTileClickListener != null) {
                        onTileClickListener.onTileClick(p.x, p.y);
                    }
                }
                
                return true;
                
            } else {
                return false;
            }
                
        }
    
    
        
        @SuppressLint("NewApi")
        @Override
        public boolean onGenericMotion(View v, MotionEvent event) {
            if (0 != (event.getSource() & InputDevice.SOURCE_CLASS_POINTER)) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_SCROLL:
                        float scaleFactor;
                        if (event.getAxisValue(MotionEvent.AXIS_VSCROLL) < 0.0f) {
                            scaleFactor = 0.8f;
                            
                        } else {
                            scaleFactor = 1.25f;
                        }
    
                        mapEditEngine.mainLoop.scaleMap(scaleFactor, scaleFactor, event.getX(), event.getY());
                        
                        return true;
                }
            }
    
            return false;
        }
    }
    
    
    public static class MapEditEngine {
        private final MainLoop mainLoop;
        private Thread mainLoopThread;
    
    
        @Inject MapEditEngine(
            final MainLoop mainLoop
        ) {
            this.mainLoop = mainLoop;
        }
        
        public void setMap(final MapData mapData) {
            mainLoop.setMap(mapData);
        }
        
        public Point getTileCoordinates(final float x, final float y) {
            return mainLoop.getTileCoordinates(x, y);
        }
        
        public void start() {
            if (mainLoop.mRunning == false) {
                mainLoopThread = new Thread(mainLoop);
                mainLoop.setRunning(true);
                mainLoopThread.start();
            }
        }
        
        public void stop() {
            if (mainLoop.mRunning) {
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
            private final String TAG = MainLoop.class.getCanonicalName();
            
            private DrawCommandBuffer drawCommandBuffer;
            
            private final SetMatrixPool setMatrixPool;
            private final DrawSpritePool spritePool;
            private final DrawTileMapPool tilemapPool;
    
            private boolean mRunning;
    
            private MapData currentMap;
            private Bitmap currentMapBitmap;
    
            private Matrix viewMatrix = new Matrix();
            private Matrix inverseViewMatrix = new Matrix();
            private Bitmap selectedTileBitmap;
            
            @Inject MainLoop(
                final DrawCommandBuffer drawCommandBuffer,
                final SetMatrixPool setMatrixPool,
                final DrawSpritePool spritePool,
                final DrawTileMapPool tilemapPool,
                final Context context
            ) {
                this.drawCommandBuffer = drawCommandBuffer;
                this.setMatrixPool = setMatrixPool;
                this.spritePool = spritePool;
                this.tilemapPool = tilemapPool;
                
                BuiltinData.load(context);
    
                viewMatrix.postScale(2, 2);
                
                selectedTileBitmap = Bitmap.createBitmap(1, 1, Config.ARGB_8888);
                selectedTileBitmap.setPixel(0, 0, Color.WHITE);
            }
    

            private float[] pts = new float[2];
            private Point point = new Point();

            private boolean highlightTile = false;

            private int highlightX = 0;

            private int highlightY = 0;
            
            public Point getTileCoordinates(float x, float y) {
                viewMatrix.invert(inverseViewMatrix);
                
                pts[0] = x;
                pts[1] = y;
                inverseViewMatrix.mapPoints(pts);
                x = pts[0];
                y = pts[1];
                
                int tileX = (int) (x / 32);
                int tileY = (int) (y / 32);
                
                point.x = tileX;
                point.y = tileY;
                
                return point;
            }

            public void setMap(final MapData mapData) {
                this.currentMap = mapData;
                currentMapBitmap = currentMap.createBitmap();
            }
    
            private void updateMapBitmap() {
                for (int y = 0; y < currentMap.getHeight(); y++) {
                    for (int x = 0; x < currentMap.getWidth(); x++) {
                        updateMapBitmap(x, y);
                    }
                }
            }


            private void updateMapBitmap(int x, int y) {
                final TileData sparseTile = 
                    currentMap.getSparseTile(x, y);
                
                if (sparseTile == null) {
                    currentMapBitmap.setPixel(x, y, currentMap.getTile(x, y).getAvgColor());
                } else {
                    currentMapBitmap.setPixel(x, y, sparseTile.getAvgColor());
                }
            }
    

            public void panMap(
                final float dx, 
                final float dy
            ) {
                viewMatrix.postTranslate(dx, dy);
            }
            
            public void scaleMap(
                final float sx, 
                final float sy, 
                final float x, 
                final float y
            ) {
                viewMatrix.postScale(sx, sy, x, y);
            }
            
            @Override
            public void run() {
                while(mRunning){
                    final ArrayList<DrawCommand> backBuffer = 
                        drawCommandBuffer.lockBackBuffer();
                    
                    if (backBuffer != null) {
                        backBuffer.add(setMatrixPool.get().set(viewMatrix));
                        backBuffer.add(tilemapPool.get().set(currentMap, currentMapBitmap));
                        
                        if (highlightTile) {
                            backBuffer.add(spritePool.get().set(
                                selectedTileBitmap, 
                                0, 0, 1, 1, 
                                highlightY * 32, 
                                highlightX * 32, 
                                (highlightX * 32) + 32, 
                                (highlightY * 32) + 32));
                        }
                    }
                    
                    drawCommandBuffer.unlockBackBuffer();
                }
            }
    
            public void setRunning(final boolean b) {
                mRunning = b;
            }

            public void invalidateMapData() {
                updateMapBitmap();
            }

            public void invalidateMapData(int x, int y) {
                updateMapBitmap(x, y);
            }

            public void setDrawCommandBuffer(DrawCommandBuffer cmdBuffer) {
                this.drawCommandBuffer = cmdBuffer;
            }

            public void highlightTile(boolean highlighted, int x, int y) {
                this.highlightTile = highlighted;
                this.highlightX = x;
                this.highlightY = y;
            }
        }

        public void invalidateMapData() {
            mainLoop.invalidateMapData();
        }

        public void invalidateMapData(int x, int y) {
            mainLoop.invalidateMapData(x, y);
        }

        public void setDrawCommandBuffer(final DrawCommandBuffer cmdBuffer) {
            mainLoop.setDrawCommandBuffer(cmdBuffer);
        }
    }

    @Inject private MapEditEngine mapEditEngine;
    private OnTileClickListener onTileClickListener;
    private boolean freezeMapLocation;
    
    @Inject
    protected MapView(
        final Context context
    ) {
        super(context);
        init(context);
    }

    public MapView(
        final Context context, 
        final AttributeSet attrs
    ) {
        super(context, attrs);
        init(context);
    }
    
    public MapView(
        final Context context, 
        final AttributeSet attrs, 
        final int defStyle
    ) {
        super(context, attrs, defStyle);
        init(context);
    }
    
    @SuppressLint("NewApi")
    private void init(
        final Context context
    ) {    
        final ScaleGestureDetector mScaleDetector = 
            new ScaleGestureDetector(context, new ScaleMapGestureListener());
                
        final MapGestureDetector mapGestureDetector = 
            new MapGestureDetector(mScaleDetector);
        
        setOnTouchListener(mapGestureDetector); 
        setOnGenericMotionListener(mapGestureDetector); 
        
        freezeMapLocation(false);
        
        final DrawCommandBuffer cmdBuffer =
            new DrawCommandBuffer();
        
        this.setDrawCommandBuffer(cmdBuffer);
        mapEditEngine.setDrawCommandBuffer(cmdBuffer);
    }

    public void freezeMapLocation(final boolean enabled) {
        this.freezeMapLocation = enabled;
    }
    
    public void setMap(final MapData mapData) {
        mapEditEngine.setMap(mapData);
    }
    
    public void invalidateMapData() {
        mapEditEngine.invalidateMapData();
    }
    
    public void invalidateMapData(final int x, final int y) {
        mapEditEngine.invalidateMapData(x, y);
    }
    
    public void setOnTileClickListener(final OnTileClickListener onTileClickListener) {
        this.onTileClickListener = onTileClickListener;
    }
    
    public void stop() {
        mapEditEngine.stop();
    }

    public void start() {
        mapEditEngine.start();
    }
    
    public void highlightTile(final boolean highlighted, final int x, final int y) {
        this.mapEditEngine.mainLoop.highlightTile(highlighted, x, y);
    }
}
