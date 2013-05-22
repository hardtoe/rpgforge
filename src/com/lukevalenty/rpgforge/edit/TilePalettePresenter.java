package com.lukevalenty.rpgforge.edit;

import java.util.ArrayList;

import com.lukevalenty.rpgforge.data.TileData;

import de.greenrobot.event.EventBus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Spinner;

public class TilePalettePresenter {
    private GridView gridView;
    private ArrayList<? extends PaletteItem> allTiles = new ArrayList<PaletteItem>();

    private int tilePaletteTileSize = 64;

    private int currentSelectedPositionInTilePalette = -1;
    private BaseAdapter tilePaletteAdapter;
    private Spinner tileDrawerSpinner;
    private View tilePalette;
    
    private void setWidth(final int width) {
        LayoutParams layoutParams = this.gridView.getLayoutParams();
        layoutParams.width = width;
        this.gridView.setLayoutParams(layoutParams);

        layoutParams = this.tilePalette.getLayoutParams();
        layoutParams.width = width;
        this.tilePalette.setLayoutParams(layoutParams);
    }
    
    private int resizeWidth(final int dx) {
        LayoutParams layoutParams = TilePalettePresenter.this.tilePalette.getLayoutParams();
        layoutParams.width = Math.max(Math.min((int) (layoutParams.width + dx), tilePaletteTileSize * 16), tilePaletteTileSize);
        TilePalettePresenter.this.tilePalette.setLayoutParams(layoutParams);
        
        layoutParams = TilePalettePresenter.this.gridView.getLayoutParams();
        layoutParams.width = Math.max(Math.min((int) (layoutParams.width + dx), tilePaletteTileSize * 16), tilePaletteTileSize);
        TilePalettePresenter.this.gridView.setLayoutParams(layoutParams);
        
        return layoutParams.width;
    }
    
    public TilePalettePresenter(
        final Context context, 
        final EventBus eventBus, 
        final View tilePalette,
        final Spinner tileDrawerSpinner, 
        final GridView gridView
    ) {
        this.gridView = gridView;
        this.tilePalette = tilePalette;
        this.tileDrawerSpinner = tileDrawerSpinner;
        
        final ArrayAdapter<String> spinnerAdapter = 
            new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        spinnerAdapter.add("Tiles");
        spinnerAdapter.add("Events");

        tileDrawerSpinner.setAdapter(spinnerAdapter);
        
        
        this.tilePaletteTileSize = 
            (int) (context.getResources().getDisplayMetrics().density * 48);
        
        this.tilePaletteAdapter = new BaseAdapter() {
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
                final PaletteItem tileData =
                    allTiles.get(position);

                ImageView tileView;
                
                if (convertView == null) {
                    tileView = 
                        new ImageView(context);
                    
                } else {
                    tileView = 
                        (ImageView) convertView;
                }
                
                if (currentSelectedPositionInTilePalette == position) {
                    tileView.setBackgroundColor(Color.WHITE);
                } else {
                    tileView.setBackgroundColor(Color.TRANSPARENT);
                }
                
                tileView.setLayoutParams(new GridView.LayoutParams(tilePaletteTileSize, tilePaletteTileSize));
                tileView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                tileView.setPadding(4, 4, 4, 4);
                
                tileView.setImageDrawable(tileData.getPreview());
                
                return tileView;
            }
        };

        
        gridView.setAdapter(tilePaletteAdapter);
        
        this.gridView.setNumColumns(4);
        setWidth(4 * tilePaletteTileSize);
        
        gridView.setOnTouchListener(new OnTouchListener() {
            private float lastX;
            private float lastY;
            private float startX;
            private float startY;

            private boolean paletteSwipeDetected = false;
            private boolean paletteSwipeImpossible = false;
            
            private int focusPosition;
            
            @Override
            public boolean onTouch(
                final View v, 
                final MotionEvent e
            ) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    startX = e.getX();
                    startY = e.getY();
                    lastX = e.getX();
                    lastY = e.getY();
                    paletteSwipeDetected = false;
                    paletteSwipeImpossible = false;
                    return false;
                    
                } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
                    
                    float dx = Math.abs(startX - e.getX());
                    float dy = Math.abs(startY - e.getY());
                    
                    if (paletteSwipeDetected == false){
                        if (dx > tilePaletteTileSize && dx > dy) {
                            paletteSwipeDetected = true;

                            focusPosition = gridView.getFirstVisiblePosition();
                            
                        } else if (dy > tilePaletteTileSize && dy > dx) {
                            paletteSwipeImpossible = true;
                        }
                        
                    } else if (paletteSwipeImpossible == true) {
                        return false;
                        
                    } 
                    
                    if (paletteSwipeDetected) {
                        final float resizeDx = (e.getX() - lastX);
                        
                        
                        int width = resizeWidth((int) resizeDx);
                        
                        final int newColumnCount = Math.max(Math.min(width / tilePaletteTileSize, 16), 1);
                        gridView.setNumColumns(newColumnCount);

                        lastX = e.getX();
                        lastY = e.getY();
                        
                        gridView.setSelection(focusPosition);
                        
                        return true;
                    }

                    return false;
                    

                } else if (e.getAction() == MotionEvent.ACTION_UP) {
                    if (paletteSwipeDetected) {
                        return true;
                        
                    } else {
                        return false;
                    }
                    
                } else {
                    return false;
                }
            }
        });

        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(
                final AdapterView<?> parent, 
                final View view, 
                final int position,
                final long row
            ) {
                currentSelectedPositionInTilePalette = position;
                tilePaletteAdapter.notifyDataSetChanged();
                
                final ListAdapter adapter = 
                    (ListAdapter) parent.getAdapter();
                
                final Object paletteItem = 
                    adapter.getItem(position);
                
                if (paletteItem instanceof TileData) {
                    final TileData tile =
                        (TileData) paletteItem;
                    
                    eventBus.post(new TileSelectedEvent(tile));   
                }             
            }
        });
    }

    public void setTiles(final ArrayList<? extends PaletteItem> allTiles) {
        this.allTiles = allTiles;
        tilePaletteAdapter.notifyDataSetChanged();
    }
}
