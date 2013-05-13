package com.lukevalenty.rpgforge.edit;

import com.lukevalenty.rpgforge.data.TileData;

import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import de.greenrobot.event.EventBus;

public class MapGestureDetector implements OnTouchListener {
    private final ScaleGestureDetector mScaleDetector;
    private final EventBus eventBus = EventBus.getDefault();
    
    private float mPreviousX;
    private float mPreviousY;
    private int pointerId = -1;

    private Tool currentTool = Tool.MOVE;
    private TileData currentTile = null;
    
    public MapGestureDetector(
        final ScaleGestureDetector mScaleDetector
    ) {
        this.mScaleDetector = mScaleDetector;
        eventBus.register(this);
    }
    
    public void onEvent(final ToolSelectedEvent e) {
        currentTool = e.tool();
    }
    
    public void onEvent(final TileSelectedEvent e) {
        currentTile = e.tile();
    }
    
    @Override
    public boolean onTouch(
        final View v, 
        final MotionEvent e
    ) {
        if (currentTool == Tool.MOVE) {
            mScaleDetector.onTouchEvent(e);
            
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                mPreviousX = e.getX();
                mPreviousY = e.getY();
                pointerId = e.getPointerId(0);
                return true;
                
            } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
                float x = e.getX();
                float y = e.getY();            
                
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;
                
                if (pointerId == e.getPointerId(0) && e.getPointerCount() == 1) {
                    eventBus.post(new PanMapEvent((int) dx, (int) dy)); 
                }
                
                mPreviousX = x;
                mPreviousY = y;
                pointerId = e.getPointerId(0);
                
                return true;
                
            } else {
                return false;
            }
        } else if (
            currentTool == Tool.DRAW && 
            currentTile != null && 
            e.getPointerCount() == 1
        ) {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                eventBus.post(new DrawTileEvent(currentTile, (int) e.getX(), (int) e.getY()));
                return true;
                
            } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
                eventBus.post(new DrawTileEvent(currentTile, (int) e.getX(), (int) e.getY()));
                return true;
                
            } else {
                return false;
            }
            
        } else if (
            currentTool == Tool.EYEDROP && 
            currentTile != null && 
            e.getPointerCount() == 1
        ) {
            final EyedropEvent event = 
                new EyedropEvent((int) e.getX(), (int) e.getY());
            
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                eventBus.post(event);
                currentTile = event.tile();
                return true;
                
            } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
                eventBus.post(event);
                currentTile = event.tile();
                return true;
                
            } else {
                return false;
            }
            
        } else if (
            currentTool == Tool.FILL && 
            currentTile != null && 
            e.getPointerCount() == 1
        ) {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                eventBus.post(new FillTileEvent(currentTile, (int) e.getX(), (int) e.getY()));
                return true;
                
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}