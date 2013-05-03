package com.lukevalenty.rpgforge;

import java.util.ArrayList;

import com.google.inject.Inject;
import com.lukevalenty.rpgforge.graphics.DrawCommand;
import com.lukevalenty.rpgforge.graphics.DrawCommandBuffer;
import com.lukevalenty.rpgforge.graphics.DrawSprite;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    public static final String TAG = GameView.class.getName();

    private Renderer renderer;
    private Thread renderThread;
    
    @Inject private DrawCommandBuffer drawCommandBuffer;
    
    @Inject GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }
    
    public GameView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        init(context);
    }
    
    private void init(Context context) {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        renderer = new Renderer(holder, context);
    }

    public class Renderer implements Runnable {
        private SurfaceHolder mHolder;
        private boolean mRunning;
        private Paint paint;
        private long duration;
        
        public Renderer(
            final SurfaceHolder holder, 
            final Context context
        ){
            mHolder = holder;
            paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(1);
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

            c.drawColor(Color.BLACK);
            
            for (int i = 0; i < frontBuffer.size(); i++) {
                final DrawCommand drawCommand =
                    frontBuffer.get(i);
                
                if (drawCommand instanceof DrawSprite) {
                    DrawSprite drawSprite = 
                        (DrawSprite) drawCommand;
                    
                    c.drawBitmap(drawSprite.texture(), drawSprite.src(), drawSprite.dst(), paint);
                }
            }
            
            duration = System.nanoTime() - startTime;
            
            drawCommandBuffer.unlockFrontBuffer();
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
