package com.lukevalenty.rpgforge.edit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ScaleGestureDetector;

import com.google.inject.Inject;
import com.lukevalenty.rpgforge.engine.GameView;

public class MapView extends GameView {

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
    }
}
