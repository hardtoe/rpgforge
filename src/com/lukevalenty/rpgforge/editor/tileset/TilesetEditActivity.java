package com.lukevalenty.rpgforge.editor.tileset;

import roboguice.inject.InjectView;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.lukevalenty.rpgforge.BaseActivity;
import com.lukevalenty.rpgforge.R;
import com.lukevalenty.rpgforge.RpgForgeApplication;
import com.lukevalenty.rpgforge.data.MapData;
import com.lukevalenty.rpgforge.data.TileData;
import com.lukevalenty.rpgforge.data.TileSetData;
import com.lukevalenty.rpgforge.graphics.MultiDrawable;

public class TilesetEditActivity extends BaseActivity {
    private static final String TAG = 
        TilesetEditActivity.class.getCanonicalName();
    
    @InjectView(R.id.tilesetGridView) private GridView assetGridView;
    
    
    private TileSetData currentTileset;
    private BaseAdapter tileListAdapter;

    private Tab currentTab;
    
    private int assetIndex;

    private Tab passabilityTab;

    private Tab layerTab;

    private Tab viewTab;
    
    private final Paint translucentPaint = new Paint();
    private final Paint blackPaint = new Paint();
    
    private Drawable xPass = new Drawable() {        
        @Override
        public void setColorFilter(ColorFilter cf) {
            // do nothing
        }
        
        @Override
        public void setAlpha(int alpha) {
            // do nothing
        }
        
        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
        
        @Override
        public void draw(final Canvas canvas) {
            final Rect bounds = 
                getBounds();
            
            canvas.drawLine(bounds.left, bounds.top, bounds.right, bounds.bottom, translucentPaint);
            canvas.drawLine(bounds.left, bounds.bottom, bounds.right, bounds.top, translucentPaint);
            
            canvas.drawLine(bounds.left, bounds.top, bounds.right, bounds.bottom, blackPaint);
            canvas.drawLine(bounds.left, bounds.bottom, bounds.right, bounds.top, blackPaint);
        }
    };
    
    private Drawable flatLine = new Drawable() {        
        @Override
        public void setColorFilter(ColorFilter cf) {
            // do nothing
        }
        
        @Override
        public void setAlpha(int alpha) {
            // do nothing
        }
        
        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
        
        @Override
        public void draw(final Canvas canvas) {
            final Rect b = 
                getBounds();
            
            canvas.drawLine(b.left, (b.top + b.bottom) / 2f, b.right, (b.top + b.bottom) / 2f, translucentPaint);
            canvas.drawLine(b.left, (b.top + b.bottom) / 2f, b.right, (b.top + b.bottom) / 2f, blackPaint);
        }
    };
    
    private Drawable upArrow = new Drawable() {        
        @Override
        public void setColorFilter(ColorFilter cf) {
            // do nothing
        }
        
        @Override
        public void setAlpha(int alpha) {
            // do nothing
        }
        
        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
        
        @Override
        public void draw(final Canvas canvas) {
            final Rect b = 
                getBounds();
            
            canvas.drawLine((b.left + b.right) / 2f, b.top, (b.left + b.right) / 2f, b.bottom, translucentPaint);
            canvas.drawLine(b.left, (b.top + b.bottom) / 2f, (b.left + b.right) / 2f, b.top, translucentPaint);
            canvas.drawLine(b.right, (b.top + b.bottom) / 2f, (b.left + b.right) / 2f, b.top, translucentPaint);
            

            canvas.drawLine((b.left + b.right) / 2f, b.top, (b.left + b.right) / 2f, b.bottom, blackPaint);
            canvas.drawLine(b.left, (b.top + b.bottom) / 2f, (b.left + b.right) / 2f, b.top, blackPaint);
            canvas.drawLine(b.right, (b.top + b.bottom) / 2f, (b.left + b.right) / 2f, b.top, blackPaint);
        }
    };
    
    private Drawable downArrow = new Drawable() {        
        @Override
        public void setColorFilter(ColorFilter cf) {
            // do nothing
        }
        
        @Override
        public void setAlpha(int alpha) {
            // do nothing
        }
        
        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
        
        @Override
        public void draw(final Canvas canvas) {
            final Rect b = 
                getBounds();
            
            canvas.drawLine((b.left + b.right) / 2f, b.top, (b.left + b.right) / 2f, b.bottom, translucentPaint);
            canvas.drawLine(b.left, (b.top + b.bottom) / 2f, (b.left + b.right) / 2f, b.bottom, translucentPaint);
            canvas.drawLine(b.right, (b.top + b.bottom) / 2f, (b.left + b.right) / 2f, b.bottom, translucentPaint);
            

            canvas.drawLine((b.left + b.right) / 2f, b.top, (b.left + b.right) / 2f, b.bottom, blackPaint);
            canvas.drawLine(b.left, (b.top + b.bottom) / 2f, (b.left + b.right) / 2f, b.bottom, blackPaint);
            canvas.drawLine(b.right, (b.top + b.bottom) / 2f, (b.left + b.right) / 2f, b.bottom, blackPaint);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tileset_edit_layout);

        translucentPaint.setColor(Color.argb(0xc0, 0xff, 0xff, 0xff));
        translucentPaint.setStrokeWidth(dpToPx(10));
        translucentPaint.setStrokeCap(Cap.ROUND);
        
        blackPaint.setColor(Color.argb(0xc0, 0x00, 0x00, 0x00));
        blackPaint.setStrokeWidth(dpToPx(6));
        blackPaint.setStrokeCap(Cap.ROUND);
        
        
        tileListAdapter = new BaseAdapter() {     
            @Override
            public View getView(
                final int position, 
                final View convertView, 
                final ViewGroup parent
            ) {
                final TileData tileData =
                    currentTileset.getTiles().get(position);

                ImageView tileView;
                
                if (convertView == null) {
                    tileView = 
                        new ImageView(TilesetEditActivity.this);
                    
                    tileView.setLayoutParams(new GridView.LayoutParams(dpToPx(64), dpToPx(64)));
                    tileView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    tileView.setPadding(4, 4, 4, 4);
                    
                } else {
                    tileView = 
                        (ImageView) convertView;
                }
                
                if (currentTab == passabilityTab) {
                    if (tileData.isPassable()) {
                        tileView.setImageDrawable(tileData.getPreview());
                        
                    } else {      
                        tileView.setImageDrawable(new MultiDrawable(tileData.getPreview(), xPass));
                    }
                    
                } else if (currentTab == layerTab) {
                    if (tileData.getLayer() == 0) {
                        tileView.setImageDrawable(new MultiDrawable(tileData.getPreview(), downArrow));
                        
                    } else if (tileData.getLayer() == 1) {
                        tileView.setImageDrawable(new MultiDrawable(tileData.getPreview(), flatLine));
                        
                    } else {
                        tileView.setImageDrawable(new MultiDrawable(tileData.getPreview(), upArrow));
                        
                    }
                    
                } else {
                    tileView.setImageDrawable(tileData.getPreview());
                }
                
                return tileView;
            }
            
            @Override
            public long getItemId(
                final int position
            ) {
                return 0;
            }
            
            @Override
            public Object getItem(
                final int position
            ) {
                return currentTileset.getTiles().get(position);
            }
            
            @Override
            public int getCount() {
                return currentTileset.getTiles().size();
            }
        };
        

        handleIntent(getIntent(), savedInstanceState);
        
        assetGridView.setAdapter(tileListAdapter);
        

        
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        

        viewTab = getActionBar().newTab().setText("View");
        passabilityTab = getActionBar().newTab().setText("Passability");
        layerTab = getActionBar().newTab().setText("Layer");
        
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
                tileListAdapter.notifyDataSetChanged();
            }
            
            @Override
            public void onTabReselected(
                final Tab tab, 
                final FragmentTransaction ft
            ) {
                // do nothing
            }
        };

        getActionBar().addTab(viewTab.setTabListener(tabListener));
        getActionBar().addTab(passabilityTab.setTabListener(tabListener));
        getActionBar().addTab(layerTab.setTabListener(tabListener));
        
        assetGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(
                final AdapterView<?> parent, 
                final View view, 
                final int position,
                final long id
            ) {
                final TileData tileData =
                    currentTileset.getTiles().get(position);
                
                if (currentTab == passabilityTab) {
                    tileData.setPassable(!tileData.isPassable());
                    
                } else if (currentTab == layerTab) {
                    tileData.setLayer((tileData.getLayer() + 1) % 3);
                }

                tileListAdapter.notifyDataSetChanged();
            }
        });
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "MapEditActivity.onNewIntent");
        handleIntent(intent, null);
    }
    
    private void handleIntent(
        final Intent intent, 
        final Bundle savedInstanceState
    ) {
        Log.d(TAG, "MapEditActivity.handleIntent - " + intent);
        
        if (intent.hasExtra("ASSET_INDEX")) {
            final int assetIndex = intent.getIntExtra("ASSET_INDEX", 0);
            setTileset(assetIndex);
        
        } else if (
            savedInstanceState != null && 
            savedInstanceState.containsKey("ASSET_INDEX")
        ) {
            setTileset(savedInstanceState.getInt("ASSET_INDEX"));
            
        } else {
            setTileset(0);
        }

        Log.d(TAG, "HANDLE INTENT FINISHED");
    }
    
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        outState.putInt("ASSET_INDEX", assetIndex);
    }
    
    private void setTileset(int assetIndex) {
        this.assetIndex = assetIndex;
        
        this.currentTileset = 
            RpgForgeApplication.getDb().getTileSets().get(assetIndex);
        
        tileListAdapter.notifyDataSetChanged();
    }

}
