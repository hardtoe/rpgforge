package com.lukevalenty.rpgforge.engine;


import com.google.inject.Inject;
import com.lukevalenty.rpgforge.R;
import com.lukevalenty.rpgforge.R.menu;
import com.lukevalenty.rpgforge.engine.input.OnScreenDPad;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;


import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;

public class GameActivity extends RoboFragmentActivity {
    @Inject private GameEngine gameEngine;
    @InjectView(R.id.gameView) private GameView gameView;
    @InjectView(R.id.onScreenDPad) private OnScreenDPad onScreenDPad;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameview);
        
        gameEngine.addGameInput(onScreenDPad);
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
