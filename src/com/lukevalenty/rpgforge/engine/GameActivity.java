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
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;

public class GameActivity extends BaseActivity {
    @Inject private GameEngine gameEngine;
    @InjectView(R.id.gameView) private GameView gameView;
    @InjectView(R.id.onScreenDPad) private OnScreenDPad onScreenDPad;
    @InjectView(R.id.onScreenActionPad) private OnScreenActionPad onScreenActionPad;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameview);
        
        gameEngine.addGameInput(new GameInput() {
            @Override
            public boolean up() {
                return onScreenDPad.up();
            }
            
            @Override
            public boolean right() {
                return onScreenDPad.right();
            }
            
            @Override
            public boolean left() {
                return onScreenDPad.left();
            }
            
            @Override
            public boolean down() {
                return onScreenDPad.down();
            }
            
            @Override
            public boolean back() {
                return onScreenActionPad.back();
            }
            
            @Override
            public boolean action() {
                return onScreenActionPad.action();
            }
        });
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
