package com.lukevalenty.rpgforge.engine.input;

public interface GameInput {
    public boolean up();
    public boolean down();
    public boolean left();
    public boolean right();
    
    public boolean action();
    public boolean back();
}
