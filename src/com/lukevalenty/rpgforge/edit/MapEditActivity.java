package com.lukevalenty.rpgforge.edit;


import com.google.inject.Inject;
import com.lukevalenty.rpgforge.GameView;
import com.lukevalenty.rpgforge.R;

import de.greenrobot.event.EventBus;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;


import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;

public class MapEditActivity extends RoboFragmentActivity {
    @InjectView(R.id.mapView) private GameView mapView;
    @Inject private MapEditEngine mapEditEngine;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    
    private EventBus eventBus = EventBus.getDefault();
    
    private static final int TOOL_MOVE = 0;
    private static final int TOOL_DRAW = 1;
    
    public int toolState = TOOL_MOVE;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapedit);

        mScaleDetector = 
            new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                /**
                 * This is the active focal point in terms of the viewport. Could be a local
                 * variable but kept here to minimize per-frame allocations.
                 */
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
            });
        
        
        mapView.setOnTouchListener(new OnTouchListener() {
            private float mPreviousX;
            private float mPreviousY;
            private int pointerId = -1;
            
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
        });
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return false;
                
            case R.id.menu_move:   
                Log.d(this.getClass().getCanonicalName(), "MOVE MOVE MOVE");
                return true;
            
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override public void onResume() {
        super.onResume();
        mapEditEngine.start();
    }
    
    @Override public void onPause() {
        super.onPause();
        mapEditEngine.stop();
    }
}
