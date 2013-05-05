package com.lukevalenty.rpgforge.edit;

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

    public MapGestureDetector(final ScaleGestureDetector mScaleDetector) {
        this.mScaleDetector = mScaleDetector;
    }
    
    @Override
    public boolean onTouch(
        final View v, 
        final MotionEvent e
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
    }
}