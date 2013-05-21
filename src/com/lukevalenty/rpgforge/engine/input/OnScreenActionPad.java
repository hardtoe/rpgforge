package com.lukevalenty.rpgforge.engine.input;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class OnScreenActionPad extends View implements GameInput {
    private Paint defaultPaint;
    private Paint activePaint;
    private Rect downRect, rightRect;
    private boolean down, right;
    
    public OnScreenActionPad(
        final Context context, 
        final AttributeSet attrs, 
        final int defStyle
    ) {
        super(context, attrs, defStyle);
        init();
    }

    public OnScreenActionPad(
        final Context context, 
        final AttributeSet attrs
    ) {
        super(context, attrs);
        init();
    }

    public OnScreenActionPad(
        final Context context
    ) {
        super(context);
        init();
    }
    
    private void init() {
        defaultPaint = new Paint();
        defaultPaint.setColor(Color.argb(0x44, 0xff, 0xff, 0xff));
        
        activePaint = new Paint();
        activePaint.setColor(Color.argb(0x88, 0xff, 0xff, 0xff));
    }
    
    @Override
    protected void onSizeChanged(
        final int width, 
        final int height, 
        final int oldWidth, 
        final int oldHeight        
    ) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        
        downRect = new Rect(
            (int) (width * 0.33f), 
            (int) (height * 0.67f), 
            (int) (width * 0.67f), 
            (int) (height));
        
        rightRect = new Rect(
            (int) (width * 0.67f), 
            (int) (height * 0.33f), 
            (int) (width), 
            (int) (height * 0.67f));
    }
    
    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
     
        c.drawRect(downRect, getPaint(down));
        c.drawRect(rightRect, getPaint(right));
    }
    
    private Paint getPaint(boolean active) {
        return active ? activePaint : defaultPaint;
    }

    @Override
    public boolean onTouchEvent(
        final MotionEvent e
    ) {
        final int x = 
            (int) e.getX();
        
        final int y = 
            (int) e.getY();
        
        final boolean active = 
            e.getAction() == MotionEvent.ACTION_DOWN ||
            e.getAction() == MotionEvent.ACTION_MOVE;
        
        down = downRect.contains(x, y) & active;
        right = rightRect.contains(x, y) & active;
        
        this.invalidate();
        
        return true;
    }

    @Override
    public boolean up() {
        return false;
    }

    @Override
    public boolean down() {
        return false;
    }

    @Override
    public boolean left() {
        return false;
    }

    @Override
    public boolean right() {
        return false;
    }

    @Override
    public boolean action() {
        return down;
    }

    @Override
    public boolean back() {
        return right;
    }
}
