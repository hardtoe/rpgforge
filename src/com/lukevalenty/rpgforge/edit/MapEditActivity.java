package com.lukevalenty.rpgforge.edit;


import java.io.File;
import java.util.ArrayList;

import com.google.inject.Inject;
import com.lukevalenty.rpgforge.BaseActivity;
import com.lukevalenty.rpgforge.DialogUtil;
import com.lukevalenty.rpgforge.R;
import com.lukevalenty.rpgforge.RpgForgeApplication;
import com.lukevalenty.rpgforge.DialogUtil.StringPromptListener;
import com.lukevalenty.rpgforge.data.BuiltinData;
import com.lukevalenty.rpgforge.data.RpgDatabase;
import com.lukevalenty.rpgforge.data.RpgDatabaseLoader;
import com.lukevalenty.rpgforge.data.TileData;
import com.lukevalenty.rpgforge.data.MapData;
import com.lukevalenty.rpgforge.engine.GameActivity;
import com.lukevalenty.rpgforge.engine.GameView;

import de.greenrobot.event.EventBus;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class MapEditActivity extends BaseActivity {
    private String activeDatabaseFilename = "defaultRpgDatabase";
    
    public static final String TAG = MapEditActivity.class.getName();
    
    @InjectView(R.id.tilePalette) private View tilePalette; 
    @InjectView(R.id.gridView1) private GridView tileList;
    @InjectView(R.id.tileDrawerSpinner) private Spinner tileDrawerSpinner; 
    @InjectView(R.id.mapView) private GameView mapView;
    @Inject private MapEditEngine mapEditEngine;
    
    @Inject private EventBus eventBus;

    @Inject private RpgDatabaseLoader loader;
    
    private Tool currentTool = Tool.MOVE;
    private BaseAdapter tilePaletteAdapter;

    private ArrayList<? extends PaletteItem> allTiles;
    private BaseAdapter mapSelectionAdapter;

    private MapSelectionPresenter mapSelectionPresenter;

    private TilePalettePresenter tilePalettePresenter;

    

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "MapEditActivity.onNewIntent");
        Log.d(TAG, "SAVING FILE: " + activeDatabaseFilename);
        loader.save(this, activeDatabaseFilename, RpgForgeApplication.getDb());
        handleIntent(intent);
        tilePaletteAdapter.notifyDataSetChanged(); 
    }
    
    private void handleIntent(Intent intent) {
        Log.d(TAG, "MapEditActivity.handleIntent - " + intent);
        
        final String intentDatabaseFilename = 
            Base64.encodeToString(intent.getExtras().getString("PROJECT_NAME").getBytes(), Base64.DEFAULT);
        
        if (!intentDatabaseFilename.equals(activeDatabaseFilename)) {
            activeDatabaseFilename = 
                intentDatabaseFilename;
            
            final File databaseFile = 
                getFileStreamPath(activeDatabaseFilename);
                
            RpgDatabase rpgDatabase;
            
            if (databaseFile.exists()) {
                rpgDatabase = loader.load(this, activeDatabaseFilename);
                
            } else {
                rpgDatabase = BuiltinData.createNewDatabase(this);
            }
              
            RpgForgeApplication.setDb(rpgDatabase);

            eventBus.post(new SelectMapEvent(0, rpgDatabase.getMaps().getFirst()));
            
            allTiles = 
                rpgDatabase.getAllTiles();       
            
            if (mapSelectionAdapter != null) {
                mapSelectionAdapter.notifyDataSetChanged();
            }
        }

        Log.d(TAG, "HANDLE INTENT FINISHED");
    }
    
    
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "== ON CREATE ==" + this.hashCode());
        setContentView(R.layout.mapedit);

        eventBus.register(this);
        
        mapEditEngine.start();
        
        handleIntent(getIntent());

        
        tilePalettePresenter =
            new TilePalettePresenter(this, eventBus, tilePalette, tileDrawerSpinner, tileList, allTiles);
        

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {     
            mapSelectionPresenter =
                new MapSelectionPresenter(this, eventBus, getActionBar());
        }      
        
        final ScaleGestureDetector mScaleDetector = 
            new ScaleGestureDetector(this, new ScaleMapGestureListener());
                
        
        mapView.setOnTouchListener(new MapGestureDetector(mScaleDetector)); 

        

        
        Log.d(TAG, "ONCREATE FINISHED");
    }
    
    public void onEvent(final TileSelectedEvent e) {
        /*
        Toast t = new Toast(this);
        ImageView tileView = new ImageView(this);
        tileView.setImageBitmap(e.tile().bitmap());
        t.setView(tileView);
        t.setDuration(Toast.LENGTH_SHORT);
        t.show();
*/
        currentTool = Tool.DRAW;
        eventBus.post(new ToolSelectedEvent(currentTool));
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onEvent(final ToolSelectedEvent e) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            invalidateOptionsMenu();
        }
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (currentTool == Tool.MOVE) {
                setActiveActionIcon(menu, R.id.menu_move, R.drawable.ic_action_move);
                
            } else if (currentTool == Tool.DRAW) {
                setActiveActionIcon(menu, R.id.menu_draw, R.drawable.ic_action_draw);
                
            } else if (currentTool == Tool.FILL) {
                setActiveActionIcon(menu, R.id.menu_fill, R.drawable.ic_action_fill);
                
            } else if (currentTool == Tool.EYEDROP) {
                setActiveActionIcon(menu, R.id.menu_eyedrop, R.drawable.ic_action_eyedrop);
            }
        }
        
        return true;
    }

    private void setActiveActionIcon(
        final Menu menu, 
        final int menuItemId,
        final int iconId
    ) {
        final MenuItem moveButton = 
            menu.findItem(menuItemId);

        final Bitmap activeMoveBitmap = 
            createActiveActionIcon(iconId);
        
        moveButton.setIcon(new BitmapDrawable(getResources(), activeMoveBitmap));
    }

    private Bitmap createActiveActionIcon(final int resourceId) {
        final Bitmap src = 
            BitmapFactory.decodeResource(getResources(), resourceId);
        
        final Bitmap dst = 
            Bitmap.createBitmap(
                src.getWidth(), 
                src.getHeight(), 
                Bitmap.Config.ARGB_8888);
        
        for (int x = 0; x < dst.getWidth(); x++) {
            for (int y = 0; y < dst.getHeight(); y++) {
                final int srcPixel = src.getPixel(x, y);
                
                if (srcPixel != 0) {
                    dst.setPixel(x, y, (srcPixel & 0xff000000) | 0xFFBB33);
                }
            }
        }
        
        return dst;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
                return true;
                
            case R.id.menu_playtest:
                Intent intent = new Intent(this, GameActivity.class);
                //intent.putExtra("PROJECT_NAME", projectName);
                startActivity(intent);
                return true;
                
            case R.id.menu_new:
                handleCreateNewMap();
                return true;
                
            case R.id.menu_move:   
                currentTool = Tool.MOVE;
                eventBus.post(new ToolSelectedEvent(currentTool));
                return true;
                
            case R.id.menu_eyedrop:
                currentTool = Tool.EYEDROP;
                eventBus.post(new ToolSelectedEvent(currentTool));
                return true;
                
            case R.id.menu_draw:
                currentTool = Tool.DRAW;
                eventBus.post(new ToolSelectedEvent(currentTool));
                return true;
                
            case R.id.menu_fill:   
                currentTool = Tool.FILL;
                eventBus.post(new ToolSelectedEvent(currentTool));
                return true;
                
            case R.id.menu_deletemap:
                handleDeleteCurrentMap();
                return true;
                
            case R.id.menu_renamemap:
                handleRenameCurrentMap();
                return true;
                
            case R.id.menu_resizemap:
                showResizeMapDialog();
                return true;
            
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleRenameCurrentMap() {
        DialogUtil.showStringPrompt(this, "New Name", "Rename Map", "Cancel", new StringPromptListener() {          
            @Override
            public void onAccept(final String value) {
                currentMap.setName(value);
                eventBus.post(new SelectMapEvent(currentMapIndex, currentMap).setMapListChanged());
            }
        });
    }
    
    private void handleDeleteCurrentMap() {
        final AlertDialog.Builder builder = 
            new AlertDialog.Builder(MapEditActivity.this);
         
        if (RpgForgeApplication.getDb().getMaps().size() == 1) {
            builder
                .setMessage("Cannot delete the only map in the project.")
                .setPositiveButton("Ok", 
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // do nothing
                        }
                    });

            builder.create().show();
            
        } else {
            builder
            .setMessage("Are you sure you want to delete this map?  It cannot be undone.")
                .setPositiveButton("Delete Map", 
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            final MapData mapToDelete = 
                                currentMap;
                            
                            RpgForgeApplication.getDb().removeMap(mapToDelete);
                            
                            final int mapIndexToShow = 
                                Math.max(currentMapIndex - 1, 0);
                            
                            final MapData mapToShow = 
                                RpgForgeApplication.getDb().getMaps().get(mapIndexToShow);
                            
                            eventBus.post(new SelectMapEvent(mapIndexToShow, mapToShow).setMapDeleted()); 
                        }
                    })
                .setNegativeButton("Cancel", 
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // do nothing
                        }
                    });
   
            builder.create().show();
        }
    }

    private void handleCreateNewMap() {
        showNewMapDialog(new NewMapDialogListener() {    
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onAccept(String mapName, int width, int height) {
                MapData newMap = new MapData(width, height); 
                newMap.fill(RpgForgeApplication.getDb().getDefaultTile());
                
                newMap.setName(mapName);
                RpgForgeApplication.getDb().addMap(newMap);
                
                eventBus.post(new SelectMapEvent(RpgForgeApplication.getDb().getMaps().size() - 1, newMap).setNewMapAdded());   
            }
        });
    }

    private MapData currentMap;
    private int currentMapIndex;
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onEvent(
        final SelectMapEvent e
    ) {
        currentMap = e.map();
        currentMapIndex = e.mapIndex();
        
        if (e.mapListChanged()) {
            mapSelectionAdapter.notifyDataSetChanged(); 

            if (android.os.Build.VERSION.SDK_INT >= 11) {
                getActionBar().setSelectedNavigationItem(e.mapIndex());
            }
        }
    }
    
    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
        return;
    }
    
    @Override 
    public void onResume() {
        super.onResume();
        mapEditEngine.start();
    }
    
    @Override 
    public void onPause() {
        super.onPause();
        mapEditEngine.stop();
        Log.d(TAG, "SAVING FILE: " + activeDatabaseFilename);
        loader.save(this, activeDatabaseFilename, RpgForgeApplication.getDb());
    }
    
    @Override 
    public void onDestroy() {
        super.onDestroy();
        mapEditEngine.stop();
    }
    
    @Override 
    public void onRestart() {
        super.onRestart();
    }
    
    @Override 
    public void onStop() {
        super.onStop();
        mapEditEngine.stop();
    }


    public void showResizeMapDialog() {
        final AlertDialog.Builder builder = 
            new AlertDialog.Builder(this);
        
        final LayoutInflater inflater = 
            this.getLayoutInflater();
        
        final View view = 
            inflater.inflate(R.layout.map_info, null);
              
        final TextView mapWidth = 
            (TextView) view.findViewById(R.id.resizeWidthField);
        
        mapWidth.setText(Integer.toString(currentMap.getWidth()));
        
        final TextView mapHeight = 
            (TextView) view.findViewById(R.id.resizeHeightField);
        
        mapHeight.setText(Integer.toString(currentMap.getHeight()));
        
        builder.setView(view)
           .setPositiveButton("Resize Map", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int id) {
                   final String widthString = 
                       mapWidth.getText().toString();
                   
                   final int width = 
                       widthString.length() == 0 ? 20 : Integer.parseInt(widthString);
                   
                   final String heightString = 
                       mapHeight.getText().toString();
                   
                   final int height =
                       heightString.length() == 0 ? 20 : Integer.parseInt(heightString);
                   
                   eventBus.post(new ResizeMapEvent(width, height));
               }
           })
           .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                   // do nothing
               }
           });
        
        builder.create().show();
    }
    
    public void showNewMapDialog(
        final NewMapDialogListener listener
    ) {
        final AlertDialog.Builder builder = 
            new AlertDialog.Builder(this);
        
        final LayoutInflater inflater = 
            this.getLayoutInflater();
        
        final View view = 
            inflater.inflate(R.layout.new_map_dialog, null);
        
        final TextView mapNameField = 
            (TextView) view.findViewById(R.id.mapNameField);
        
        final TextView mapWidth = 
            (TextView) view.findViewById(R.id.widthField);
        
        final TextView mapHeight = 
            (TextView) view.findViewById(R.id.heightField);
        
        builder.setView(view)
           .setPositiveButton("Create Map", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int id) {
                   final String mapName = 
                       mapNameField.getText().toString().trim();
                   
                   final String widthString = 
                       mapWidth.getText().toString();
                   
                   final int width = 
                       widthString.length() == 0 ? 20 : Integer.parseInt(widthString);
                   
                   final String heightString = 
                       mapHeight.getText().toString();
                   
                   final int height =
                       heightString.length() == 0 ? 20 : Integer.parseInt(heightString);
                    
                   listener.onAccept(
                       mapName, 
                       width,
                       height);
               }
           })
           .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                   // do nothing
               }
           });
        
        builder.create().show();
    }
    
    public static interface NewMapDialogListener {
        public void onAccept(final String mapName, final int width, final int height);
    }
}
