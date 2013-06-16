package com.lukevalenty.rpgforge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class NewButton extends View {
    private Paint paint;

    public NewButton(
        final Context context, 
        final AttributeSet attrs, 
        final int defStyle
    ) {
        super(context, attrs, defStyle);
        init();
    }

    public NewButton(
        final Context context, 
        final AttributeSet attrs
    ) {
        super(context, attrs);
        init();
    }

    public NewButton(
        final Context context
    ) {
        super(context);
        init();
    }
    
    private void init() {
        paint = new Paint();
        paint.setStrokeWidth(dpToPx(10));
        paint.setColor(Color.GRAY);
        paint.setStyle(Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas c) {
        int xMid = c.getWidth() / 2;
        int yMid = c.getHeight() / 2;
        
        int length = Math.min(c.getHeight(), c.getWidth()) / 3;
        int halfLen = length / 2;
        
        c.drawRect(0, 0, c.getWidth(), c.getHeight(), paint);
        
        c.drawLine(xMid - halfLen, yMid, xMid + halfLen, yMid, paint);
        c.drawLine(xMid, yMid - halfLen, xMid, yMid + halfLen, paint);
    }

    protected int dpToPx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }
}
