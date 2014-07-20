package com.lukevalenty.rpgforge.engine;

import android.util.Log;

public abstract class GameObjectComponent {
    public abstract void update(
        final FrameState frameState, 
        final GlobalGameState globalState);
    
    public void onMessage(final GameMessage m) {
        // do nothing by default
    }
    
    private final String TAG = 
        this.getClass().getSimpleName();
    
    protected void log(final String msg) {
        Log.d(TAG, msg);
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public void init(
        final GameObject gameObject, 
        final GlobalGameState globalState
    ) {
        // do nothing by default
    }
}
