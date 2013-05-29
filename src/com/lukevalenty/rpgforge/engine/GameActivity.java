package com.lukevalenty.rpgforge.engine;


import com.google.inject.Inject;
import com.lukevalenty.rpgforge.BaseActivity;
import com.lukevalenty.rpgforge.R;
import com.lukevalenty.rpgforge.R.menu;
import com.lukevalenty.rpgforge.engine.input.GameInput;
import com.lukevalenty.rpgforge.engine.input.OnScreenActionPad;
import com.lukevalenty.rpgforge.engine.input.OnScreenDPad;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.RelativeLayout;


import static android.view.KeyEvent.*;

public class GameActivity extends BaseActivity {
    @Inject private GameEngine gameEngine;
    @InjectView(R.id.gameView) private GameView gameView;
    @InjectView(R.id.onScreenDPad) private OnScreenDPad onScreenDPad;
    @InjectView(R.id.onScreenActionPad) private OnScreenActionPad onScreenActionPad;
    
    boolean up;
    boolean down;
    boolean left;
    boolean right;
    boolean action;
    boolean back;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameview);
        
        gameEngine.addGameInput(new GameInput() {
            @Override
            public boolean up() {
                return onScreenDPad.up() || up;
            }
            
            @Override
            public boolean right() {
                return onScreenDPad.right() || right;
            }
            
            @Override
            public boolean left() {
                return onScreenDPad.left() || left;
            }
            
            @Override
            public boolean down() {
                return onScreenDPad.down() || down;
            }
            
            @Override
            public boolean back() {
                return onScreenActionPad.back() || back;
            }
            
            @Override
            public boolean action() {
                return onScreenActionPad.action() || action;
            }
        });
        
        gameView.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(
                final View v, 
                final int keyCode, 
                final KeyEvent e
            ) {
                final boolean onDown = 
                    e.getAction() == KeyEvent.ACTION_DOWN;
                
                final boolean onUp = 
                    e.getAction() == KeyEvent.ACTION_UP;
                
                    if (onDown) {
                        down = true;
                        
                    } else if (onUp) {
                        down = false;
                    }
                
                return true;
            }
        });       
    }

    @Override 
    public boolean onKeyDown(
        final int keyCode, 
        final KeyEvent event
    ) {
        if (isContainedIn(keyCode, KEYCODE_DPAD_DOWN, KEYCODE_S)) {
            down = true;
            return true;
            
        } else if (isContainedIn(keyCode, KEYCODE_DPAD_UP, KEYCODE_W)) {
            up = true;
            return true;
            
        } else if (isContainedIn(keyCode, KEYCODE_DPAD_LEFT, KEYCODE_A)) {
            left = true;
            return true;
            
        } else if (isContainedIn(keyCode, KEYCODE_DPAD_RIGHT, KEYCODE_D)) {
            right = true;
            return true;
            
        } else if (isContainedIn(keyCode, KEYCODE_BUTTON_A, KEYCODE_BUTTON_X, KEYCODE_BUTTON_1)) {
            action = true;
            return true;
            
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override 
    public boolean onKeyUp(
        final int keyCode, 
        final KeyEvent event
    ) {
        if (isContainedIn(keyCode, KEYCODE_DPAD_DOWN, KEYCODE_S)) {
            down = false;
            return true;
            
        } else if (isContainedIn(keyCode, KEYCODE_DPAD_UP, KEYCODE_W)) {
            up = false;
            return true;
            
        } else if (isContainedIn(keyCode, KEYCODE_DPAD_LEFT, KEYCODE_A)) {
            left = false;
            return true;
            
        } else if (isContainedIn(keyCode, KEYCODE_DPAD_RIGHT, KEYCODE_D)) {
            right = false;
            return true;
            
        } else if (isContainedIn(keyCode, KEYCODE_BUTTON_A, KEYCODE_BUTTON_X, KEYCODE_BUTTON_1)) {
            action = false;
            return true;
            
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }
    
    private boolean isContainedIn(
        final int keyCode,
        final int... keyCodes
    ) {
        for (int i = 0; i < keyCodes.length; i++) {
            if (keyCode == keyCodes[i]) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override public void onResume() {
        super.onResume();
        gameEngine.start();
    }
    
    @Override public void onPause() {
        super.onPause();
        gameEngine.stop();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
