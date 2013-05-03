package com.lukevalenty.rpgforge;


import com.google.inject.Inject;
import roboguice.activity.RoboFragmentActivity;


import android.os.Bundle;
import android.view.Menu;

public class GameActivity extends RoboFragmentActivity {
    @Inject private GameView gameView;
    @Inject private GameEngine gameEngine;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(gameView);
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
