package com.lukevalenty.rpgforge.edit;

import com.lukevalenty.rpgforge.data.EventData;
import com.lukevalenty.rpgforge.data.TileData;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnGenericMotionListener;
import android.view.View.OnTouchListener;
import de.greenrobot.event.EventBus;

@SuppressLint("NewApi")
public class MapGestureDetector implements OnTouchListener, OnGenericMotionListener {
    private final ScaleGestureDetector mScaleDetector;
    private final EventBus eventBus = EventBus.getDefault();
    
    private float mPreviousX;
    private float mPreviousY;
    private int pointerId = -1;

    private Tool currentTool = Tool.MOVE;
    private PaletteItem currentTile = null;
    
    public MapGestureDetector(
        final ScaleGestureDetector mScaleDetector
    ) {
        this.mScaleDetector = mScaleDetector;
        eventBus.register(this);
    }
    
    public void onEvent(final ToolSelectedEvent e) {
        currentTool = e.tool();
    }
    
    public void onEvent(final PaletteItemSelectedEvent e) {
        if (e.tile() instanceof EventData) {
            currentTile = ((EventData) e.tile()).create();
        } else {
            currentTile = e.tile();
        }
    }
    
    @SuppressLint("NewApi")
    @Override
    public boolean onTouch(
        final View v, 
        final MotionEvent e
    ) {
        if (
            currentTool == Tool.MOVE || 
            ((e.getButtonState() & MotionEvent.BUTTON_SECONDARY) != 0) // right-click and drag to move
        ) {
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
            e.getPointerCount() == 1
        ) {
            final EyedropEvent event = 
                new EyedropEvent((int) e.getX(), (int) e.getY());
            
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                eventBus.post(event);
                return true;
                
            } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
                eventBus.post(event);
                return true;
                
            } else {
                return false;
            }
            
        } else if (
            currentTool == Tool.FILL && 
            currentTile != null && 
            currentTile instanceof TileData &&
            e.getPointerCount() == 1
        ) {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                eventBus.post(new FillTileEvent((TileData) currentTile, (int) e.getX(), (int) e.getY()));
                return true;
                
            } else {
                return false;
            }
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

                    eventBus.post(new ScaleMapEvent(scaleFactor, event.getX(), event.getY()));
                    
                    return true;
            }
        }

        return false;
    }
}