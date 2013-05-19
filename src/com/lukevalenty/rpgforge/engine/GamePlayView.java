package com.lukevalenty.rpgforge.engine;

import com.google.inject.Inject;

import android.content.Context;
import android.util.AttributeSet;

public class GamePlayView extends GameView {

    @Inject GamePlayView(
        final Context context
    ) {
        super(context);
    }

    public GamePlayView(
        final Context context, 
        final AttributeSet attrs
    ) {
        super(context, attrs);
    }
    
    public GamePlayView(
        final Context context, 
        final AttributeSet attrs, 
        final int defStyle
    ) {
        super(context, attrs, defStyle);
    }



    @Override
    protected void onMeasure(
        final int widthMeasureSpec, 
        final int heightMeasureSpec     
    ) { 
        // 512 x 384 effective resolution
        final float scaleFactor =
            (float) (getResources().getDisplayMetrics().heightPixels / 384.0);
        
        this.setMeasuredDimension(
            (int) (512 * scaleFactor), 
            getResources().getDisplayMetrics().heightPixels);
    }

}
