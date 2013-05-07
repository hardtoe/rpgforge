package com.lukevalenty.rpgforge.edit;


import java.util.ArrayList;

import com.google.inject.Inject;
import com.lukevalenty.rpgforge.GameView;
import com.lukevalenty.rpgforge.R;
import com.lukevalenty.rpgforge.data.TileData;

import de.greenrobot.event.EventBus;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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

public class MapEditActivity extends RoboFragmentActivity {
    public static final String TAG = MapEditActivity.class.getName();
    
    @InjectView(R.id.gridView1) private GridView tilePalette;
    @InjectView(R.id.mapView) private GameView mapView;
    @Inject private MapEditEngine mapEditEngine;
    
    private EventBus eventBus = EventBus.getDefault();
    
    private Tool currentTool = Tool.MOVE;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapedit);
        
        final ScaleGestureDetector mScaleDetector = 
            new ScaleGestureDetector(this, new ScaleMapGestureListener());
                
        mapView.setOnTouchListener(new MapGestureDetector(mScaleDetector)); 
        
        final ArrayList<TileData> allTiles = 
                new ArrayList<TileData>();

        TestData.load(this);
        
        allTiles.addAll(TestData.TILESET_A1.getTiles());
        allTiles.addAll(TestData.TILESET_A2.getTiles());
        allTiles.addAll(TestData.TILESET_A3.getTiles());
        allTiles.addAll(TestData.TILESET_A4.getTiles());
        allTiles.addAll(TestData.TILESET_A5.getTiles());

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
            
        tilePalette.setAdapter(new BaseAdapter() {
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
        });
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
                currentTool = Tool.MOVE;
                eventBus.post(new ToolSelectedEvent(currentTool));
                return true;
                
            case R.id.menu_draw:
                currentTool = Tool.DRAW;
                eventBus.post(new ToolSelectedEvent(currentTool));
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
