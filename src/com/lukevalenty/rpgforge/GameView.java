package com.lukevalenty.rpgforge;

import java.util.ArrayList;

import roboguice.RoboGuice;

import com.google.inject.Inject;
import com.lukevalenty.rpgforge.data.AutoTileData;
import com.lukevalenty.rpgforge.data.MapData;
import com.lukevalenty.rpgforge.data.TileData;
import com.lukevalenty.rpgforge.edit.ScaleMapEvent;
import com.lukevalenty.rpgforge.graphics.DrawCommand;
import com.lukevalenty.rpgforge.graphics.DrawCommandBuffer;
import com.lukevalenty.rpgforge.graphics.DrawSprite;
import com.lukevalenty.rpgforge.graphics.DrawTileMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    public static final String TAG = GameView.class.getName();

    private Renderer renderer;
    private Thread renderThread;
    
    @Inject private DrawCommandBuffer drawCommandBuffer;
    
    @Inject GameView(
        final Context context
    ) {
        super(context);
        init(context);
    }

    public GameView(
        final Context context, 
        final AttributeSet attrs
    ) {
        super(context, attrs);
        init(context);
    }
    
    public GameView(
        final Context context, 
        final AttributeSet attrs, 
        final int defStyle
    ) {
        super(context, attrs, defStyle);
        init(context);
    }
    
    private void init(
        final Context context
    ) {
        RoboGuice.getInjector(context).injectMembers(this);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        renderer = new Renderer(holder, context);
    }

    public class Renderer implements Runnable {
        
        private SurfaceHolder mHolder;
        private boolean mRunning;
        private Paint paint;
        private long duration;

        private Rect src;
        private Rect dst;

        private long frameIndex;

        private int tileSize = 32;
        private float mapScale;

        
        
        public Renderer(
            final SurfaceHolder holder, 
            final Context context
        ){
            mHolder = holder;
            
            paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(1);
            
            src = new Rect();
            dst = new Rect();
        }

        @Override
        public void run() {
            while(mRunning){
                Canvas c = mHolder.lockCanvas(null);
                
                if (c != null) {             
                    doDraw(c);
                    mHolder.unlockCanvasAndPost(c);
                    
                    updateFramerate();
                }
            }
        }

        private void doDraw(final Canvas c) {
            final ArrayList<DrawCommand> frontBuffer = 
                drawCommandBuffer.lockFrontBuffer();
          
            long startTime = System.nanoTime();
            frameIndex = startTime / 16000000L;
            
            c.drawColor(Color.BLACK);
            
            for (int i = 0; i < frontBuffer.size(); i++) {
                final DrawCommand drawCommand =
                    frontBuffer.get(i);
                
                if (drawCommand instanceof DrawSprite) {
                    final DrawSprite drawSprite = 
                        (DrawSprite) drawCommand;
                    
                    c.drawBitmap(drawSprite.texture(), drawSprite.src(), drawSprite.dst(), paint);
                    
                } else if (drawCommand instanceof DrawTileMap) {
                    final DrawTileMap drawTilemap =
                        (DrawTileMap) drawCommand;

                    c.setMatrix(drawTilemap.matrix());
                    
                    final MapData map =
                        drawTilemap.map();
                    
                    for (int x = 0; x < map.getWidth(); x++) {
                        for (int y = 0; y < map.getHeight(); y++) {
                            final TileData tile =
                                map.getTile(x, y);
                            
                            if (tile instanceof AutoTileData) {
                                drawAutoTile(map, drawTilemap, (AutoTileData) tile, c, x, y);
                            } else {
                                drawTile(drawTilemap, tile, c, x, y);
                            }
                        }
                    }
                    
                }
            }
            
            duration = System.nanoTime() - startTime;
            
            drawCommandBuffer.unlockFrontBuffer();
        }

        private void drawTile(
            final DrawTileMap cmd,
            final TileData tile,
            final Canvas c, 
            final int x,
            final int y
        ) {
            dst.top = (y * tileSize);
            dst.bottom = dst.top + tileSize;
            dst.left = (x * tileSize);
            dst.right = dst.left + tileSize;
            
            c.drawBitmap(tile.bitmap(), tile.src(frameIndex), dst, paint);
        }

        private void drawAutoTile(
            final MapData map, 
            final DrawTileMap cmd,
            final AutoTileData tile,
            final Canvas c, 
            final int x,
            final int y
        ) {
            TileData topLeft =  map.getTile(x - 1, y - 1);
            TileData top =      map.getTile(x    , y - 1);
            TileData topRight = map.getTile(x + 1, y - 1);
            
            TileData left =     map.getTile(x - 1, y);
            TileData right =    map.getTile(x + 1, y);
            
            TileData botLeft =  map.getTile(x - 1, y + 1);
            TileData bot =      map.getTile(x    , y + 1);
            TileData botRight = map.getTile(x + 1, y + 1);
            
            drawAutoTileCorner(cmd, tile, c, 0, 0, x, y, left,  topLeft,  top);
            drawAutoTileCorner(cmd, tile, c, 1, 0, x, y, right, topRight, top);
            drawAutoTileCorner(cmd, tile, c, 0, 1, x, y, left,  botLeft,  bot);
            drawAutoTileCorner(cmd, tile, c, 1, 1, x, y, right, botRight, bot);
        }

        
        private void drawAutoTileCorner(
            final DrawTileMap cmd, 
            final AutoTileData autoTile, 
            final Canvas c,
            final int xTargetCornerIndex,
            final int yTargetCornerIndex, 
            final int x, 
            final int y, 
            final TileData verticalTile, 
            final TileData cornerTile,
            final TileData horizontalTile
        ) {
            int xCornerOffset = 0;
            int yCornerOffset = 0;
            
            if (
                autoTile.isCompatibleWith(verticalTile) &&
                autoTile.isCompatibleWith(horizontalTile)
            ) {
                if (autoTile.isCompatibleWith(cornerTile)) {
                    xCornerOffset = autoTileOffset(1 + (xTargetCornerIndex * 2));
                    yCornerOffset = autoTileOffset(1 + (yTargetCornerIndex * 2)) + 32;
                            
                } else {
                    xCornerOffset = (xTargetCornerIndex * 16) + 32;
                    yCornerOffset = (yTargetCornerIndex * 16);
                }
                
            } else if (
                autoTile.isCompatibleWith(verticalTile) &&
                !autoTile.isCompatibleWith(horizontalTile)
            ) {
                xCornerOffset = autoTileOffset(1 + (xTargetCornerIndex * 2));
                yCornerOffset = autoTileOffset(yTargetCornerIndex * 2) + 32;
                
            } else if (
                !autoTile.isCompatibleWith(verticalTile) &&
                autoTile.isCompatibleWith(horizontalTile)
            ) {
                xCornerOffset = autoTileOffset(xTargetCornerIndex * 2);
                yCornerOffset = autoTileOffset(1 + (yTargetCornerIndex * 2)) + 32;
                
            } else if (
                !autoTile.isCompatibleWith(verticalTile) &&
                !autoTile.isCompatibleWith(horizontalTile)
            ) {
                xCornerOffset = autoTileOffset(xTargetCornerIndex * 2);
                yCornerOffset = autoTileOffset(yTargetCornerIndex * 2) + 32;
            }
            
            dst.top = (y * tileSize) + ((tileSize / 2) * yTargetCornerIndex);
            dst.bottom = dst.top + (tileSize / 2);
            dst.left = (x * tileSize) + ((tileSize / 2) * xTargetCornerIndex);
            dst.right = dst.left + (tileSize / 2);
            
            final Rect autoTileSrc = 
                autoTile.src(frameIndex);
            
            src.top = autoTileSrc.top + yCornerOffset;
            src.bottom = src.top + 16;
            
            src.left = autoTileSrc.left + xCornerOffset;
            src.right = src.left + 16;
            
            c.drawBitmap(autoTile.bitmap(), src, dst, paint);
        }

        private int autoTileOffset(final int i) {
            switch (i) {
                case 0: return 0 * 16;
                case 1: return 2 * 16;
                case 2: return 3 * 16;
                case 3: return 1 * 16;
                default: return 0;
            }
        }

        int counter;
        private void updateFramerate() {
            counter++;
            
            if (counter > 60 || counter < 0) {
                counter = 0;
                Log.d(TAG, "frametime in ms = " + (duration/1000000.0));
            }
        }

        public void setRunning(boolean b) {
            mRunning = b;
        }
    }
    

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        Log.d(TAG, "surface changed");
    }
    
    public void surfaceDestroyed(SurfaceHolder arg0) {
        Log.d(TAG, "surface destroyed");
        
        renderer.setRunning(false);
        
        try {
            renderThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }
    
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surface created");
        
        renderThread = new Thread(renderer);
        renderer.setRunning(true);
        renderThread.start();
    }
}
