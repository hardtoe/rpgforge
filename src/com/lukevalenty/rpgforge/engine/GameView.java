package com.lukevalenty.rpgforge.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import roboguice.RoboGuice;

import com.google.inject.Inject;
import com.lukevalenty.rpgforge.data.AutoTileData;
import com.lukevalenty.rpgforge.data.CharacterData;
import com.lukevalenty.rpgforge.data.EnemyEventData;
import com.lukevalenty.rpgforge.data.EventData;
import com.lukevalenty.rpgforge.data.MapData;
import com.lukevalenty.rpgforge.data.NpcEventData;
import com.lukevalenty.rpgforge.data.TileData;
import com.lukevalenty.rpgforge.editor.map.ScaleMapEvent;
import com.lukevalenty.rpgforge.engine.battle.BattleZoneEventData;
import com.lukevalenty.rpgforge.graphics.DrawCommand;
import com.lukevalenty.rpgforge.graphics.DrawCommandBuffer;
import com.lukevalenty.rpgforge.graphics.DrawInGameUiWindow;
import com.lukevalenty.rpgforge.graphics.SetMatrix;
import com.lukevalenty.rpgforge.graphics.DrawSprite;
import com.lukevalenty.rpgforge.graphics.DrawTileMap;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Canvas.EdgeType;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.LinearGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    public static final String TAG = GameView.class.getName();

    private Renderer renderer;
    private Thread renderThread;
    
    @Inject private DrawCommandBuffer drawCommandBuffer;
    
    private boolean debug;
    
    @Inject
    protected GameView(
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
    
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }
    
    public void setDrawCommandBuffer(final DrawCommandBuffer drawCommandBuffer) {
        this.drawCommandBuffer = drawCommandBuffer;
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
        private static final float DEFRINGE_CORRECTION = 1f / 2f;
        
        private SurfaceHolder mHolder;
        private boolean mRunning;
        private Paint paint;
        private long duration;

        private Rect src;
        private RectF dst;

        private long frameIndex;

        private int tileSize = 32;
        
        private float[] pts = new float[2];
        private Matrix matrix;
        private final Matrix inverseMatrix = new Matrix();
        private int numTiles;

        private Paint linePaint;
        private Paint dialogPaint;
        private Paint dialogBgPaint;
        private Paint dialogBorderPaint;
        private Paint battleZonePaint;
        
        private Bitmap finger;
        
        public Renderer(
            final SurfaceHolder holder, 
            final Context context
        ){
            mHolder = holder;
            
            paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(1);
            
            linePaint = new Paint();
            linePaint.setColor(Color.WHITE);
            linePaint.setStrokeWidth(2);
            linePaint.setStyle(Style.STROKE);
            
            dialogPaint = new Paint();
            dialogPaint.setColor(Color.WHITE);
            dialogPaint.setTextSize(16);
            dialogPaint.setTypeface(Typeface.MONOSPACE);
            dialogPaint.setShadowLayer(1, 1, 1, Color.BLACK);
            dialogPaint.setAntiAlias(true);
            
            dialogBgPaint = new Paint();
            //dialogBgPaint.setColor(Color.rgb(0, 0, 0x63));
            //dialogBgPaint.setStyle(Style.FILL);
            dialogBgPaint.setShader(new LinearGradient(0, 0, 0, 400, Color.rgb(0x7a, 0x7a, 0xd5), Color.rgb(0, 0, 0x39), TileMode.CLAMP)); 
            
            dialogBorderPaint = new Paint();
            dialogBorderPaint.setColor(Color.WHITE);
            dialogBorderPaint.setStyle(Style.STROKE);
            dialogBorderPaint.setStrokeWidth(4);
            dialogBorderPaint.setShadowLayer(2, 2, 2, Color.BLACK);
            dialogBorderPaint.setStrokeCap(Cap.ROUND);
            dialogBorderPaint.setStrokeJoin(Join.ROUND);

            battleZonePaint = new Paint();
            battleZonePaint.setColor(Color.argb(0x20, 0xff, 0, 0));
            battleZonePaint.setStyle(Style.FILL);
            
            try {
                finger = BitmapFactory.decodeStream(context.getAssets().open("finger.png"));
                
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
            
            src = new Rect();
            dst = new RectF();
        }

        @Override
        public void run() {
            while(mRunning){
                final ArrayList<DrawCommand> frontBuffer = 
                    drawCommandBuffer.lockFrontBuffer();
                
                if (frontBuffer != null) { 
                    final Canvas c = 
                        mHolder.lockCanvas();
                    
                    if (c != null) {      
                        doDraw(c, frontBuffer);
                        mHolder.unlockCanvasAndPost(c);
                    }
                }
                
                drawCommandBuffer.unlockFrontBuffer();
            }
        }

        private void doDraw(final Canvas c, final ArrayList<DrawCommand> frontBuffer) {
          
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
                    
                    final MapData map =
                        drawTilemap.map();
                    
                    if (map != null) {
                        matrix.invert(inverseMatrix);
                        
                        pts[0] = 0;
                        pts[1] = 0;
                        inverseMatrix.mapPoints(pts);
                        float xMin = pts[0];
                        float yMin = pts[1];
                        
                        int xMinTile = Math.max((int) (xMin / 32), 0);
                        int yMinTile = Math.max((int) (yMin / 32), 0);
                        
    
                        pts[0] = c.getWidth();
                        pts[1] = c.getHeight();
                        inverseMatrix.mapPoints(pts);
                        float xMax = pts[0];
                        float yMax = pts[1];
              
                        int xMaxTile = Math.min((int) (xMax / 32) + 1, map.getWidth());
                        int yMaxTile = Math.min((int) (yMax / 32) + 1, map.getHeight());
                        
                        numTiles = (xMaxTile - xMinTile) * (yMaxTile - yMinTile);
                        
                        if (numTiles > 5000) {
                            src.left = 0;
                            src.top = 0;
                            src.right = drawTilemap.bitmap().getWidth();
                            src.bottom = drawTilemap.bitmap().getHeight();
                            
                            dst.left = src.left;
                            dst.top = src.top;
                            dst.right = src.right * tileSize + DEFRINGE_CORRECTION;
                            dst.bottom = src.bottom * tileSize + DEFRINGE_CORRECTION;
                            c.drawBitmap(drawTilemap.bitmap(), src, dst, null);
                            
                        } else {
                            for (int y = yMinTile; y < yMaxTile; y++) {
                                for (int x = xMinTile; x < xMaxTile; x++) {
                                    if (drawTilemap.isLower()) {
                                        final TileData tile =
                                            map.getTile(x, y);
                                        
                                        if (tile != null) {
                                            if (tile instanceof AutoTileData) {
                                                drawAutoTile(map, drawTilemap, (AutoTileData) tile, c, x, y);
                                            } else {
                                                drawTile(tile, c, x, y);
                                            }
                                        }
                                    }
                                    
                                    
                                    final TileData sparseTile =
                                        map.getSparseTile(x, y);

                                    if (sparseTile != null) {
                                        if (
                                            (drawTilemap.isLower() && sparseTile.getLayer() <= 1) ||
                                            (drawTilemap.isUpper() && sparseTile.getLayer() > 1)
                                        ) {
                                            if (sparseTile instanceof AutoTileData) {
                                                drawAutoTile(map, drawTilemap, (AutoTileData) sparseTile, c, x, y);
                                            } else {
                                                drawTile(sparseTile, c, x, y);
                                            }            
                                        }
                                    }
                                    
                                    final EventData eventData =
                                        map.getEvent(x, y);
                                    
                                    if (eventData != null && debug) {
                                        if (eventData instanceof NpcEventData) {
                                            // TODO: REFACTOR INTO ONE
                                            final CharacterData charData = 
                                                ((NpcEventData) eventData).getCharacterData();
                                            
                                            if (charData != null) {
                                                src.top = charData.src().top;
                                                src.bottom = src.top + 48;
                                                src.left = charData.src().left + 32;
                                                src.right = src.left + 32;
                                                
                                                dst.top = (y * tileSize) - 16;
                                                dst.bottom = dst.top + tileSize + 16;
                                                dst.left = (x * tileSize);
                                                dst.right = dst.left + tileSize;
                                                
                                                c.drawBitmap(charData.bitmap(), src, dst, null);
                                            }
                                        
                                        } else if (eventData instanceof EnemyEventData) {
                                            // TODO: REFACTOR INTO ONE
                                            final CharacterData charData = 
                                                ((EnemyEventData) eventData).getCharacterData();
                                            
                                            if (charData != null) {
                                                src.top = charData.src().top;
                                                src.bottom = src.top + 48;
                                                src.left = charData.src().left + 32;
                                                src.right = src.left + 32;
                                                
                                                dst.top = (y * tileSize) - 16;
                                                dst.bottom = dst.top + tileSize + 16;
                                                dst.left = (x * tileSize);
                                                dst.right = dst.left + tileSize;
                                                
                                                c.drawBitmap(charData.bitmap(), src, dst, null);
                                            }

                                        } else {
                                            dst.top = (y * tileSize) + 3;
                                            dst.bottom = dst.top + tileSize - 6;
                                            dst.left = (x * tileSize) + 3;
                                            dst.right = dst.left + tileSize - 6;
                                            
                                            c.drawRect(dst, linePaint); 
                                        }
                                        
                                    }
                                }
                            }

                            if (debug) {
                                for (final EventData eventData : map.getEvents()) {
                                    if (eventData instanceof BattleZoneEventData) {
                                        final BattleZoneEventData bz = 
                                            (BattleZoneEventData) eventData;
                                        
                                        c.drawRect(
                                            bz.getX1(), 
                                            bz.getY1(), 
                                            bz.getX2(), 
                                            bz.getY2(), 
                                            battleZonePaint); 
                                    }
                                }
                            }
                        }
                    }
                    
                } else if (drawCommand instanceof SetMatrix) {
                    matrix = ((SetMatrix) drawCommand).matrix();
                    c.setMatrix(matrix);
                
                } else if (drawCommand instanceof DrawInGameUiWindow) {
                    DrawInGameUiWindow dialog =
                        (DrawInGameUiWindow) drawCommand;
                    
                    if (dialog.isDialog()) {
                        drawInGameUiWindowDialog(c, dialog.text(), dialog.getX1(), dialog.getY1(), dialog.getX2(), dialog.getY2());
                        
                    } else if (dialog.isSelection()) {
                        drawInGameSelectionBox(c, dialog.text(), dialog.selectedOption(), dialog.getX1(), dialog.getY1(), dialog.getX2(), dialog.getY2());
                    }
                }
            }
            
            duration = System.nanoTime() - startTime;
        }

        private void drawInGameUiWindowDialog(
            final Canvas c, 
            final String[] text,
            final int x1,
            final int y1,
            final int x2,
            final int y2
        ) {
            drawInGameUiWindow(c, x1, y1, x2, y2);

            final float x = getX();
            final float y = getY();
            
            final int numlines = 
                text.length;

            final int x1_scaled = x1 * 32;
            final int y1_scaled = y1 * 32;
            
            for (int lineNumber = 0; lineNumber < numlines; lineNumber++) {
                c.drawText(
                    text[lineNumber], 
                    (x1_scaled + 16) - x, 
                    ((y1_scaled + 24) + (16 * lineNumber)) - y, 
                    dialogPaint);
            }      
        }

        private void drawInGameSelectionBox(
                final Canvas c, 
                final String[] options,
                final int selectedOption,
                final int x1,
                final int y1,
                final int x2,
                final int y2
        ) {
            drawInGameUiWindow(c, x1, y1, x2, y2);

            final float x = getX();
            final float y = getY();
            
            final int numlines = 
                    options.length;

            final int x1_scaled = x1 * 32;
            final int y1_scaled = y1 * 32;
            
            for (int lineNumber = 0; lineNumber < numlines; lineNumber++) {
                final float xText = 
                    (x1_scaled + 36) - x;
                
                final float yText = 
                    ((y1_scaled + 24) + (16 * lineNumber)) - y;
                
                c.drawText(
                    options[lineNumber], 
                    xText, 
                    yText, 
                    dialogPaint);
                
                if (lineNumber == selectedOption) {
                    c.drawBitmap(finger, xText - 24, yText - 10, null);
                }
            }    
        }
        
        private void drawInGameUiWindow(
            final Canvas c, 
            final int x1,
            final int y1, 
            final int x2, 
            final int y2
        ) {
            final float x = getX();
            final float y = getY();
            
            final int x1_scaled = x1 * 32;
            final int y1_scaled = y1 * 32;
            final int x2_scaled = x2 * 32;
            final int y2_scaled = y2 * 32;
            
            c.drawRect(
                x1_scaled - x, 
                y1_scaled - y, 
                x2_scaled - x, 
                y2_scaled - y, 
                dialogBgPaint);
            
            c.drawRect(
                (x1_scaled - x) + 2, 
                (y1_scaled - y) + 2, 
                (x2_scaled - x) - 2, 
                (y2_scaled - y) - 2, 
                dialogBorderPaint);
        }

        private float getY() {
            matrix.getValues(matrixValues);
            return matrixValues[Matrix.MTRANS_Y] / matrixValues[Matrix.MSCALE_Y];
        }

        private float getX() {
            matrix.getValues(matrixValues);
            return matrixValues[Matrix.MTRANS_X] / matrixValues[Matrix.MSCALE_X];
        }

        float[] matrixValues = new float[9];
        
        private void drawTile(
            final TileData tile,
            final Canvas c, 
            final int x,
            final int y
        ) {
            dst.top = (y * tileSize);
            dst.bottom = dst.top + tileSize + DEFRINGE_CORRECTION;
            dst.left = (x * tileSize);
            dst.right = dst.left + tileSize + DEFRINGE_CORRECTION;
            
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
            final int layer = tile.getLayer();
            
            final TileData topLeft =  map.getTile(x - 1, y - 1, layer);
            final TileData top =      map.getTile(x    , y - 1, layer);
            final TileData topRight = map.getTile(x + 1, y - 1, layer);
            
            final TileData left =     map.getTile(x - 1, y, layer);
            final TileData right =    map.getTile(x + 1, y, layer);
            
            final TileData botLeft =  map.getTile(x - 1, y + 1, layer);
            final TileData bot =      map.getTile(x    , y + 1, layer);
            final TileData botRight = map.getTile(x + 1, y + 1, layer);
            
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
            
            final int mainTileOffset = 
                (autoTile.hasConcaveCorners() ? 32 : 0);
            
            if (
                autoTile.isCompatibleWith(verticalTile) &&
                autoTile.isCompatibleWith(horizontalTile)
            ) {
                if (autoTile.isCompatibleWith(cornerTile) || !autoTile.hasConcaveCorners()) {
                    xCornerOffset = autoTileOffset(1 + (xTargetCornerIndex * 2));
                    yCornerOffset = autoTileOffset(1 + (yTargetCornerIndex * 2)) + mainTileOffset;
                            
                } else {
                    xCornerOffset = (xTargetCornerIndex * 16) + 32;
                    yCornerOffset = (yTargetCornerIndex * 16);
                }
                
            } else if (
                autoTile.isCompatibleWith(verticalTile) &&
                !autoTile.isCompatibleWith(horizontalTile)
            ) {
                xCornerOffset = autoTileOffset(1 + (xTargetCornerIndex * 2));
                yCornerOffset = autoTileOffset(yTargetCornerIndex * 2) + mainTileOffset;
                
            } else if (
                !autoTile.isCompatibleWith(verticalTile) &&
                autoTile.isCompatibleWith(horizontalTile)
            ) {
                xCornerOffset = autoTileOffset(xTargetCornerIndex * 2);
                yCornerOffset = autoTileOffset(1 + (yTargetCornerIndex * 2)) + mainTileOffset;
                
            } else if (
                !autoTile.isCompatibleWith(verticalTile) &&
                !autoTile.isCompatibleWith(horizontalTile)
            ) {
                xCornerOffset = autoTileOffset(xTargetCornerIndex * 2);
                yCornerOffset = autoTileOffset(yTargetCornerIndex * 2) + mainTileOffset;
            }
            
            dst.top = (y * tileSize) + ((tileSize / 2) * yTargetCornerIndex);
            dst.bottom = dst.top + (tileSize / 2) + DEFRINGE_CORRECTION;
            dst.left = (x * tileSize) + ((tileSize / 2) * xTargetCornerIndex);
            dst.right = dst.left + (tileSize / 2) + DEFRINGE_CORRECTION;
            
            final Rect autoTileSrc = 
                autoTile.src(frameIndex);
            
            src.top = autoTileSrc.top + yCornerOffset;
            src.bottom = src.top + 16;
            
            src.left = autoTileSrc.left + xCornerOffset;
            src.right = src.left + 16;
            
            c.drawBitmap(autoTile.bitmap(), src, dst, paint);
        }

        private final int autoTileOffset(final int i) {
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
