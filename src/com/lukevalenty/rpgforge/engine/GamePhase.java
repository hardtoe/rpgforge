package com.lukevalenty.rpgforge.engine;

public enum GamePhase {
    /**
     * Update object internal state.  Set intent to move.
     */
    UPDATE,
    
    /**
     * Detect and handle collisions.
     */
    COLLISION,
    
    /**
     * Move game objects.
     */
    MOVE,
    
    /**
     * Setup 2D view transform and send to render thread.
     */
    PRERENDER,
    
    /**
     * Send draw commands to render thread.
     */
    RENDER;
}
