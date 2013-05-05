package com.lukevalenty.rpgforge.edit;


import com.google.inject.Inject;
import com.lukevalenty.rpgforge.GameView;
import com.lukevalenty.rpgforge.R;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ScaleGestureDetector;

public class MapEditActivity extends RoboFragmentActivity {
    public static final String TAG = MapEditActivity.class.getName();
    
    @InjectView(R.id.mapView) private GameView mapView;
    @Inject private MapEditEngine mapEditEngine;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapedit);

        final ScaleGestureDetector mScaleDetector = 
            new ScaleGestureDetector(this, new ScaleMapGestureListener());
                
        mapView.setOnTouchListener(new MapGestureDetector(mScaleDetector));        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return false;
                
            case R.id.menu_move:   
                // do something with a move here
                return true;
            
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override public void onResume() {
        super.onResume();
        mapEditEngine.start();
    }
    
    @Override public void onPause() {
        super.onPause();
        mapEditEngine.stop();
    }
}
