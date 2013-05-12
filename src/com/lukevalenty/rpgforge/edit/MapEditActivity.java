package com.lukevalenty.rpgforge.edit;


import java.io.File;
import java.util.ArrayList;

import com.google.inject.Inject;
import com.lukevalenty.rpgforge.DialogUtil;
import com.lukevalenty.rpgforge.GameView;
import com.lukevalenty.rpgforge.R;
import com.lukevalenty.rpgforge.RpgForgeApplication;
import com.lukevalenty.rpgforge.DialogUtil.StringPromptListener;
import com.lukevalenty.rpgforge.data.BuiltinData;
import com.lukevalenty.rpgforge.data.RpgDatabase;
import com.lukevalenty.rpgforge.data.RpgDatabaseLoader;
import com.lukevalenty.rpgforge.data.TileData;
import com.lukevalenty.rpgforge.data.MapData;

import de.greenrobot.event.EventBus;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ActionBar.OnNavigationListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class MapEditActivity extends RoboFragmentActivity {
    private String activeDatabaseFilename = "defaultRpgDatabase";
    
    public static final String TAG = MapEditActivity.class.getName();
    
    @InjectView(R.id.gridView1) private GridView tilePalette;
    @InjectView(R.id.mapView) private GameView mapView;
    @Inject private MapEditEngine mapEditEngine;
    
    @Inject private EventBus eventBus;

    @Inject private RpgDatabaseLoader loader;
    
    private Tool currentTool = Tool.MOVE;
    private BaseAdapter tilePaletteAdapter;

    private ArrayList<TileData> allTiles;
    private BaseAdapter mapSelectionAdapter;
    
    // FIXME: this should be moved to RpgForgeApplication
             

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
                Log.d(TAG, "FILE EXISTS: " + activeDatabaseFilename);
                
            } else {
                rpgDatabase = BuiltinData.createNewDatabase(this);
                Log.d(TAG, "FILE DOES NOT EXIST: " + activeDatabaseFilename);
            }
              
            RpgForgeApplication.setDb(rpgDatabase);

            eventBus.post(new SelectMapEvent(0, rpgDatabase.getMaps().getFirst()));
            
            allTiles = 
                rpgDatabase.getAllTiles();       
            
            if (mapSelectionAdapter != null) {
                mapSelectionAdapter.notifyDataSetChanged();
            }
        }
    }
    
    
    
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "== ON CREATE ==" + this.hashCode());
        setContentView(R.layout.mapedit);

        eventBus.register(this, SelectMapEvent.class);
        
        mapEditEngine.start();
        
        handleIntent(getIntent());

        mapSelectionAdapter =
            new BaseAdapter() {
                
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView label = new TextView(MapEditActivity.this);
                    label.setGravity(Gravity.HORIZONTAL_GRAVITY_MASK | Gravity.LEFT);
                    label.setTextSize(18);
                    label.setPadding(8, 16, 8, 16);
                    label.setText(RpgForgeApplication.getDb().getMaps().get(position).getName());
                    label.setTextColor(Color.WHITE);
                    return label;
                }
                
                @Override
                public long getItemId(int position) {
                    return System.identityHashCode(getItem(position));
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
            
                    
        final OnNavigationListener mapSelectionListener =
            new OnNavigationListener() {
                @Override
                public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                    eventBus.post(new SelectMapEvent(itemPosition, (MapData) mapSelectionAdapter.getItem(itemPosition)));
                    return true;
                }
            };
        
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getActionBar().setListNavigationCallbacks(mapSelectionAdapter, mapSelectionListener);
        
        
        
        final ScaleGestureDetector mScaleDetector = 
            new ScaleGestureDetector(this, new ScaleMapGestureListener());
                
        
        mapView.setOnTouchListener(new MapGestureDetector(mScaleDetector)); 

        

        tilePalette.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(
                final AdapterView<?> parent, 
                final View view, 
                final int position,
                final long row
            ) {
                final ListAdapter adapter = 
                    (ListAdapter) parent.getAdapter();
                
                final TileData tile =
                    (TileData) adapter.getItem(position);
                
                eventBus.post(new TileSelectedEvent(tile));
            }
        });
            
        tilePaletteAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return allTiles.size();
            }

            @Override
            public Object getItem(int position) {
                return allTiles.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(
                final int position, 
                final View convertView, 
                final ViewGroup parent
            ) {
                final TileData tileData =
                    allTiles.get(position);
                
                final ImageView tileView =
                    new ImageView(MapEditActivity.this);
                
                tileView.setLayoutParams(new GridView.LayoutParams(96, 96));
                tileView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                tileView.setPadding(4, 4, 4, 4);
                
                tileView.setImageDrawable(new Drawable() {
                    @Override
                    public void draw(final Canvas canvas) {
                        canvas.drawBitmap(tileData.bitmap(), tileData.getPreview(), getBounds(), null);
                    }

                    @Override
                    public int getOpacity() {
                        return PixelFormat.OPAQUE;
                    }

                    @Override
                    public void setAlpha(final int alpha) {
                        // do nothing
                    }

                    @Override
                    public void setColorFilter(final ColorFilter cf) {
                        // do nothing
                    }
                });
                
                
                return tileView;
            }
        };
        tilePalette.setAdapter(tilePaletteAdapter);
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
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
                return true;
                
            case R.id.menu_new:
                handleCreateNewMap();
                return true;
                
            case R.id.menu_move:   
                currentTool = Tool.MOVE;
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

        Log.d(TAG, "== ON RESUME ==" + this.hashCode());
        mapEditEngine.start();
    }
    
    @Override 
    public void onPause() {
        super.onPause();
        Log.d(TAG, "== ON PAUSE ==" + this.hashCode());
        mapEditEngine.stop();
        Log.d(TAG, "SAVING FILE: " + activeDatabaseFilename);
        loader.save(this, activeDatabaseFilename, RpgForgeApplication.getDb());
    }
    
    @Override 
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "== ON DESTROY ==" + this.hashCode());
        mapEditEngine.stop();
    }
    
    @Override 
    public void onRestart() {
        super.onRestart();
        Log.d(TAG, "== ON RESTART ==" + this.hashCode());
    }
    
    @Override 
    public void onStop() {
        super.onStop();

        Log.d(TAG, "== ON STOP ==" + this.hashCode());
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
                    
                   currentMap.resize(width, height, RpgForgeApplication.getDb().getDefaultTile());
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
