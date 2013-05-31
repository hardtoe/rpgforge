package com.lukevalenty.rpgforge.engine.input;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;

public class OnScreenDPad extends View implements GameInput {
    private Paint defaultPaint;
    private Paint activePaint;
    private Rect upDrawRect, downDrawRect, leftDrawRect, rightDrawRect;
    private Rect upHitRect, downHitRect, leftHitRect, rightHitRect;
    private boolean up, down, left, right;
    
    private boolean visible = true;
    
    public OnScreenDPad(
        final Context context, 
        final AttributeSet attrs, 
        final int defStyle
    ) {
        super(context, attrs, defStyle);
        init();
    }

    public OnScreenDPad(
        final Context context, 
        final AttributeSet attrs
    ) {
        super(context, attrs);
        init();
    }

    public OnScreenDPad(
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

    public void setVisible(final boolean visible) {
        this.visible = visible;
    }
    
    @Override
    protected void onSizeChanged(
        final int width, 
        final int height, 
        final int oldWidth, 
        final int oldHeight        
    ) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        
        upDrawRect = new Rect(
            (int) (width * 0.33f), 
            0, 
            (int) (width * 0.67f), 
            (int) (height * 0.33f));
        
        downDrawRect = new Rect(
            (int) (width * 0.33f), 
            (int) (height * 0.67f), 
            (int) (width * 0.67f), 
            (int) (height));
        
        leftDrawRect = new Rect(
            (int) (0), 
            (int) (height * 0.33f), 
            (int) (width * 0.33f), 
            (int) (height * 0.67f));
        
        rightDrawRect = new Rect(
            (int) (width * 0.67f), 
            (int) (height * 0.33f), 
            (int) (width), 
            (int) (height * 0.67f));
        
        
        upHitRect = new Rect(
            0, 
            0, 
            width, 
            (int) (height * 0.33f));
        
        downHitRect = new Rect(
            0, 
            (int) (height * 0.67f), 
            width, 
            height);
        
        leftHitRect = new Rect(
            0, 
            0, 
            (int) (width * 0.33f), 
            height);
        
        rightHitRect = new Rect(
            (int) (width * 0.67f), 
            0, 
            width, 
            height);
    }
    
    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
     
        if (visible) {
            c.drawRect(upDrawRect, getPaint(up));
            c.drawRect(downDrawRect, getPaint(down));
            c.drawRect(leftDrawRect, getPaint(left));
            c.drawRect(rightDrawRect, getPaint(right));
        }
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
        
        up = upHitRect.contains(x, y) & active;
        down = downHitRect.contains(x, y) & active;
        left = leftHitRect.contains(x, y) & active;
        right = rightHitRect.contains(x, y) & active;
        
        this.invalidate();
        
        return true;
    }
    

    @Override
    public boolean up() {
        return up;
    }

    @Override
    public boolean down() {
        return down;
    }

    @Override
    public boolean left() {
        return left;
    }

    @Override
    public boolean right() {
        return right;
    }

    @Override
    public boolean action() {
        return false;
    }

    @Override
    public boolean back() {
        return false;
    }
}
