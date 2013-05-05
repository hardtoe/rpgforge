package com.lukevalenty.rpgforge.edit;

import android.view.ScaleGestureDetector;
import de.greenrobot.event.EventBus;

public 
    class ScaleMapGestureListener 
extends
    ScaleGestureDetector.SimpleOnScaleGestureListener 
{
    private final EventBus eventBus = EventBus.getDefault();
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

        eventBus.post(new ScaleMapEvent(scaleFactor, focusX, focusY));
        eventBus.post(new PanMapEvent((int) (focusX - lastFocusX), (int) (focusY - lastFocusY))); 
        
        lastFocusX = focusX;
        lastFocusY = focusY;
        
        return true;
    }
}