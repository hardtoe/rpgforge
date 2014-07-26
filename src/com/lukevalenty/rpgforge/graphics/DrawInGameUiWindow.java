package com.lukevalenty.rpgforge.graphics;

import com.lukevalenty.rpgforge.memory.ObjectPool;

public class DrawInGameUiWindow extends DrawCommand<DrawInGameUiWindow> {
    public DrawInGameUiWindow(final ObjectPool<DrawInGameUiWindow> objectPool) {
        super(objectPool);
    }
    
    private String[] text;
    private boolean isDialog;
    private boolean isSelection;
    private int selectedOption;

    private int x1;
    private int y1;
    private int x2;
    private int y2;
    
    public DrawInGameUiWindow set(
        final int x1,
        final int y1,
        final int x2,
        final int y2,
        final String... text
    ) {
        super.init();
        isDialog = false;
        isSelection = false;
        selectedOption = 0;
        
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.text = text;
        
        return this;
    }
    
    public DrawInGameUiWindow setDialog() {
        this.isDialog = true;
        return this;
    }
    
    public DrawInGameUiWindow setSelection(final int selectedOption) {
        this.isSelection = true;
        this.selectedOption = selectedOption;
        return this;
    }
    
    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }
    
    public String[] text() {
        return text;
    }

    public boolean isDialog() {
        return isDialog;
    }

    public boolean isSelection() {
        return isSelection;
    }

    public int selectedOption() {
        return selectedOption;
    }
}
