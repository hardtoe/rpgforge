package com.lukevalenty.rpgforge.browse;

import java.io.File;

import roboguice.inject.InjectView;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.inject.Inject;
import com.lukevalenty.rpgforge.BaseActivity;
import com.lukevalenty.rpgforge.R;
import com.lukevalenty.rpgforge.RpgForgeApplication;
import com.lukevalenty.rpgforge.data.BuiltinData;
import com.lukevalenty.rpgforge.data.MapData;
import com.lukevalenty.rpgforge.data.RpgDatabase;
import com.lukevalenty.rpgforge.data.RpgDatabaseLoader;
import com.lukevalenty.rpgforge.editor.map.MapEditActivity;
import com.lukevalenty.rpgforge.editor.map.PaletteItem;
import com.lukevalenty.rpgforge.editor.map.SelectMapEvent;

public class GameOverviewActivity extends BaseActivity {
    private static final String TAG = 
        GameOverviewActivity.class.getCanonicalName();

    private String activeDatabaseFilename = "defaultRpgDatabase";
    @Inject private RpgDatabaseLoader loader;
    @InjectView(R.id.assetGridView) private GridView assetGridView;
    
    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "GameOverviewActivity.onNewIntent");
        Log.d(TAG, "SAVING FILE: " + activeDatabaseFilename);
        loader.save(this, activeDatabaseFilename, RpgForgeApplication.getDb());
        handleIntent(intent);
    }
    
    private void handleIntent(Intent intent) {
        Log.d(TAG, "GameOverviewActivity.handleIntent - " + intent);
        
        if (intent.hasExtra("PROJECT_NAME")) {
            
            final String intentDatabaseFilename = 
                Base64.encodeToString(intent.getExtras().getString("PROJECT_NAME").getBytes(), Base64.DEFAULT);
            
            if (!intentDatabaseFilename.equals(activeDatabaseFilename)) {
                activeDatabaseFilename = 
                    intentDatabaseFilename;
                
                final File databaseFile = 
                    getFileStreamPath(activeDatabaseFilename);
                    
                RpgDatabase rpgDatabase;
    
                // clean up old memory
                RpgForgeApplication.setDb(null);
                
                if (databaseFile.exists()) {
                    rpgDatabase = loader.load(this, activeDatabaseFilename);
                    
                } else {
                    rpgDatabase = BuiltinData.createNewDatabase(this);
                }
                  
                RpgForgeApplication.setDb(rpgDatabase);
            }
        }
        Log.d(TAG, "HANDLE INTENT FINISHED");
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_overview_layout);

        handleIntent(getIntent());
        
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        final TabListener tabListener = new TabListener() {    
            @Override
            public void onTabUnselected(
                final Tab tab, 
                final FragmentTransaction ft
            ) {
                
            }
            
            @Override
            public void onTabSelected(
                final Tab tab, 
                final FragmentTransaction ft
            ) {
                
            }
            
            @Override
            public void onTabReselected(
                final Tab tab, 
                final FragmentTransaction ft
            ) {
                
            }
        };
        
        getActionBar().addTab(getActionBar().newTab().setText("Maps").setTabListener(tabListener));
        getActionBar().addTab(getActionBar().newTab().setText("Tilesets").setTabListener(tabListener));
        getActionBar().addTab(getActionBar().newTab().setText("Characters").setTabListener(tabListener));
        getActionBar().addTab(getActionBar().newTab().setText("Enemies").setTabListener(tabListener));
        getActionBar().addTab(getActionBar().newTab().setText("Items").setTabListener(tabListener));
        getActionBar().addTab(getActionBar().newTab().setText("Skills").setTabListener(tabListener));
        getActionBar().addTab(getActionBar().newTab().setText("Sprites").setTabListener(tabListener));
        getActionBar().addTab(getActionBar().newTab().setText("Events").setTabListener(tabListener));
        

        final BaseAdapter assetGridAdapter = new BaseAdapter() {      
            @Override
            public View getView(
                final int position, 
                final View convertView, 
                final ViewGroup parent
            ) {
                final MapData tileData =
                    RpgForgeApplication.getDb().getMaps().get(position);

                ImageView tileView;
                
                if (convertView == null) {
                    tileView = 
                        new ImageView(GameOverviewActivity.this);
                    
                    tileView.setLayoutParams(new GridView.LayoutParams(dpToPx(200), dpToPx(200)));
                    tileView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    tileView.setPadding(4, 4, 4, 4);
                    
                } else {
                    tileView = 
                        (ImageView) convertView;
                }
                
                tileView.setImageBitmap(tileData.createBitmap(dpToPx(200)));
                
                return tileView;
            }
            
            @Override
            public long getItemId(int position) {
                return 0;
            }
            
            @Override
            public Object getItem(int position) {
                return RpgForgeApplication.getDb().getMaps().get(position);
            }
            
            @Override
            public int getCount() {
                return RpgForgeApplication.getDb().getMaps().size();
            }
        };
        assetGridView.setAdapter(assetGridAdapter);
        
        assetGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(
                final AdapterView<?> parent, 
                final View view, 
                final int position,
                final long row
            ) {
                Intent intent = new Intent(GameOverviewActivity.this, MapEditActivity.class);
                intent.putExtra("ASSET_INDEX", position);
                startActivity(intent);
                overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
                
            }
        });
    }
    
    @Override 
    public void onPause() {
        super.onPause();
        Log.d(TAG, "SAVING FILE: " + activeDatabaseFilename);
        loader.save(this, activeDatabaseFilename, RpgForgeApplication.getDb());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
        return;
    }

    private int dpToPx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }
}
