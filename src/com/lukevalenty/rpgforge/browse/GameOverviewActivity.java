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
import com.lukevalenty.rpgforge.data.MapData;
import com.lukevalenty.rpgforge.data.RpgDatabase;
import com.lukevalenty.rpgforge.data.RpgDatabaseLoader;
import com.lukevalenty.rpgforge.data.TileSetData;
import com.lukevalenty.rpgforge.editor.map.MapEditActivity;
import com.lukevalenty.rpgforge.editor.map.PaletteItem;
import com.lukevalenty.rpgforge.editor.map.SelectMapEvent;
import com.lukevalenty.rpgforge.editor.tileset.TilesetEditActivity;

public class GameOverviewActivity extends BaseActivity {
    private static final String TAG = 
        GameOverviewActivity.class.getCanonicalName();

    @InjectView(R.id.assetGridView) private GridView assetGridView;

    private static int currentTabIndex;
    
    private Tab currentTab;


    private BaseAdapter mapsGridAdapter;

    private OnItemClickListener mapsClickListener;

    private BaseAdapter tilesetsGridAdapter;

    private OnItemClickListener tilesetsClickListener;
    
    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "GameOverviewActivity.onNewIntent");
        Log.d(TAG, "SAVING FILE: " + RpgForgeApplication.getDbFile());
        RpgForgeApplication.save(this);
        handleIntent(intent);
    }
    
    private void handleIntent(Intent intent) {
        Log.d(TAG, "GameOverviewActivity.handleIntent - " + intent);

        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            final File intentDatabaseFile =
                new File(intent.getData().getPath());
            
            if (!intentDatabaseFile.getAbsolutePath().equals(RpgForgeApplication.getDbFile())) {
                RpgForgeApplication.load(this, intentDatabaseFile);
            }
            
            currentTabIndex = 0;
        }
        
        Log.d(TAG, "HANDLE INTENT FINISHED");
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_overview_layout);

        handleIntent(getIntent());
        

        Log.d(TAG, "ON CREATE: " + savedInstanceState);

        mapsGridAdapter = new BaseAdapter() {      
            @Override
            public View getView(
                final int position, 
                final View convertView, 
                final ViewGroup parent
            ) {
                final MapData mapData =
                    RpgForgeApplication.getDb().getMaps().get(position);

                ImageView mapView;
                
                if (convertView == null) {
                    mapView = 
                        new ImageView(GameOverviewActivity.this);
                    
                    mapView.setLayoutParams(new GridView.LayoutParams(dpToPx(200), dpToPx(200)));
                    mapView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    mapView.setPadding(4, 4, 4, 4);
                    
                } else {
                    mapView = 
                        (ImageView) convertView;
                }
                
                mapView.setImageBitmap(mapData.createBitmap(dpToPx(200)));
                
                return mapView;
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

        mapsClickListener = new OnItemClickListener() {
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
        };
        
        
        
        

        tilesetsGridAdapter = new BaseAdapter() {      
            @Override
            public View getView(
                final int position, 
                final View convertView, 
                final ViewGroup parent
            ) {
                final TileSetData tileSetData =
                    RpgForgeApplication.getDb().getTileSets().get(position);

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
                
                tileView.setImageBitmap(tileSetData.bitmap());
                
                return tileView;
            }
            
            @Override
            public long getItemId(int position) {
                return 0;
            }
            
            @Override
            public Object getItem(int position) {
                return RpgForgeApplication.getDb().getTileSets().get(position);
            }
            
            @Override
            public int getCount() {
                return RpgForgeApplication.getDb().getTileSets().size();
            }
        };

        tilesetsClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(
                final AdapterView<?> parent, 
                final View view, 
                final int position,
                final long row
            ) {
                Intent intent = new Intent(GameOverviewActivity.this, TilesetEditActivity.class);
                intent.putExtra("ASSET_INDEX", position);
                startActivity(intent);
                overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
            }
        };
        
        
        
        
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        final Tab mapsTab = getActionBar().newTab().setText("Maps");
        final Tab tilesetsTab = getActionBar().newTab().setText("Tilesets");
        final Tab charactersTab = getActionBar().newTab().setText("Characters");
        final Tab enemiesTab = getActionBar().newTab().setText("Enemies");
        final Tab itemsTab = getActionBar().newTab().setText("Items");
        final Tab skillsTab = getActionBar().newTab().setText("Skills");
        final Tab spritesTab = getActionBar().newTab().setText("Sprites");
        final Tab eventsTab = getActionBar().newTab().setText("Events");
        
        final TabListener tabListener = new TabListener() {    
            @Override
            public void onTabUnselected(
                final Tab tab, 
                final FragmentTransaction ft
            ) {
                // do nothing
            }
            
            @Override
            public void onTabSelected(
                final Tab tab, 
                final FragmentTransaction ft
            ) {
                currentTab = tab;
                
                if (currentTab == mapsTab) {
                    assetGridView.setAdapter(mapsGridAdapter);
                    assetGridView.setOnItemClickListener(mapsClickListener);
                    
                } else if (currentTab == tilesetsTab) {
                    assetGridView.setAdapter(tilesetsGridAdapter);
                    assetGridView.setOnItemClickListener(tilesetsClickListener);
                    
                }
            }
            
            @Override
            public void onTabReselected(
                final Tab tab, 
                final FragmentTransaction ft
            ) {
                // do nothing
            }
        };
        
        getActionBar().addTab(mapsTab.setTabListener(tabListener));
        getActionBar().addTab(tilesetsTab.setTabListener(tabListener));
        getActionBar().addTab(charactersTab.setTabListener(tabListener));
        getActionBar().addTab(enemiesTab.setTabListener(tabListener));
        getActionBar().addTab(itemsTab.setTabListener(tabListener));
        getActionBar().addTab(skillsTab.setTabListener(tabListener));
        getActionBar().addTab(spritesTab.setTabListener(tabListener));
        getActionBar().addTab(eventsTab.setTabListener(tabListener));
        
        /*
        currentTab = mapsTab;
        
        assetGridView.setAdapter(mapsGridAdapter);
        assetGridView.setOnItemClickListener(mapsClickListener);
        */

    }
    
    
    @Override 
    public void onPause() {
        super.onPause();
        Log.d(TAG, "SAVING FILE: " + RpgForgeApplication.getDbFile());
        RpgForgeApplication.save(this);
        currentTabIndex = getActionBar().getSelectedNavigationIndex();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        getActionBar().setSelectedNavigationItem(currentTabIndex);
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
