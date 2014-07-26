package com.lukevalenty.rpgforge.engine;

public enum Direction {
    UP      (0,     -1),
    DOWN    (0,     1),
    LEFT    (-1,    0),
    RIGHT   (1,     0);
    
    public final int x;
    public final int y;

    private Direction(
        final int x, 
        final int y
    ) {
        this.x = x;
        this.y = y;
    }
    
    public static Direction getDirection(final int index) {
        switch (index) {
        case 0: return UP;
        case 1: return DOWN;
        case 2: return LEFT;
        default: return RIGHT;
        }
    }
}