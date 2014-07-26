package com.lukevalenty.rpgforge.engine;


import java.util.Timer;
import java.util.TimerTask;

import com.google.inject.Inject;
import com.lukevalenty.rpgforge.BaseActivity;
import com.lukevalenty.rpgforge.R;
import com.lukevalenty.rpgforge.R.menu;
import com.lukevalenty.rpgforge.engine.input.GameInput;
import com.lukevalenty.rpgforge.engine.input.OnScreenActionPad;
import com.lukevalenty.rpgforge.engine.input.OnScreenDPad;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
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
    
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_play_layout);
        
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
        
        startTimer();
        
        final OnTouchListener timeoutListener = new OnTouchListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                if ((e.getDevice().getSources() | InputDevice.SOURCE_TOUCHSCREEN) != 0) {
                    onScreenDPad.setVisible(true);
                    onScreenActionPad.setVisible(true);
                    
                    onScreenDPad.invalidate();
                    onScreenActionPad.invalidate();
                    startTimer();
                }
                
                return false;
            }
        };
        
        gameView.setClickable(false);
        
        onScreenDPad.setOnTouchListener(timeoutListener);
        onScreenActionPad.setOnTouchListener(timeoutListener);
        gameView.setOnTouchListener(timeoutListener);
    }
   

    //private Timer fadeoutTimer;
    private Handler handler = new Handler();
    private Runnable fadeOnScreenDPad = new Runnable() {
        @SuppressLint("NewApi")
        @Override
        public void run() {
            onScreenDPad.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    onScreenDPad.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    onScreenDPad.setVisible(false);
                    onScreenDPad.invalidate();
                }
            });
            
            onScreenActionPad.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    onScreenActionPad.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    onScreenActionPad.setVisible(false);
                    onScreenActionPad.invalidate();
                }
            });
        }
    };
    
    private void startTimer() {
        handler.removeCallbacks(fadeOnScreenDPad);
        handler.postDelayed(fadeOnScreenDPad, 10000);
        
        /*
        if (fadeoutTimer != null) {
            fadeoutTimer.cancel();
            fadeoutTimer = null;
        }
        
        fadeoutTimer = new Timer();
        fadeoutTimer.schedule(new TimerTask() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                onScreenDPad.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        onScreenDPad.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                        onScreenDPad.setVisible(false);
                        onScreenDPad.invalidate();
                    }
                });
                
                onScreenActionPad.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        onScreenActionPad.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                        onScreenActionPad.setVisible(false);
                        onScreenActionPad.invalidate();
                    }
                });
            }
        }, 10000);
        */
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
        
        /*
        if (fadeoutTimer != null) {
            fadeoutTimer.cancel();
            fadeoutTimer = null;
        }
        */
        
        handler.removeCallbacks(fadeOnScreenDPad);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
