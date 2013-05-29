package com.lukevalenty.rpgforge.edit;

import java.util.ArrayList;

import com.lukevalenty.rpgforge.data.EventData;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Spinner;

public class TilePalettePresenter {
    private GridView tileGridView;
    private ArrayList<? extends PaletteItem> activePaletteItems;

    private ArrayList<? extends PaletteItem> eventPaletteItems = new ArrayList<PaletteItem>();
    
    private int paletteItemWidth;
    private int paletteItemHeight;

    private int currentSelectedPositionInTilePalette = -1;
    private BaseAdapter tilePaletteAdapter;
    private Spinner tileDrawerSpinner;
    private View tilePalette;
    
    private void setWidth(final int width) {
        LayoutParams layoutParams = this.tileGridView.getLayoutParams();
        layoutParams.width = width;
        this.tileGridView.setLayoutParams(layoutParams);

        layoutParams = this.tilePalette.getLayoutParams();
        layoutParams.width = width;
        this.tilePalette.setLayoutParams(layoutParams);
    }
    
    private int resizeWidth(final int dx) {
        LayoutParams layoutParams = TilePalettePresenter.this.tilePalette.getLayoutParams();
        layoutParams.width = Math.max(Math.min((int) (layoutParams.width + dx), paletteItemWidth * 16), paletteItemWidth);
        TilePalettePresenter.this.tilePalette.setLayoutParams(layoutParams);
        
        layoutParams = TilePalettePresenter.this.tileGridView.getLayoutParams();
        layoutParams.width = Math.max(Math.min((int) (layoutParams.width + dx), paletteItemWidth * 16), paletteItemWidth);
        TilePalettePresenter.this.tileGridView.setLayoutParams(layoutParams);
        
        return layoutParams.width;
    }
    
    public TilePalettePresenter(
        final Context context, 
        final EventBus eventBus, 
        final View tilePalette,
        final Spinner tileDrawerSpinner, 
        final GridView tileGridView,
        final ArrayList<? extends PaletteItem> tilesetPaletteItems,
        final ArrayList<? extends PaletteItem> eventPaletteItems
    ) {
        this.tileGridView = tileGridView;
        this.tilePalette = tilePalette;
        this.tileDrawerSpinner = tileDrawerSpinner;
        this.activePaletteItems = tilesetPaletteItems;
        this.eventPaletteItems = eventPaletteItems;
        
        final ArrayAdapter<String> spinnerAdapter = 
            new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        spinnerAdapter.add("Tiles");
        spinnerAdapter.add("Events");

        tileDrawerSpinner.setAdapter(spinnerAdapter);
        
        tileDrawerSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(
                final AdapterView<?> parent, 
                final View view, 
                final int position,
                final long id
            ) {
                if (id == 0) {
                    activePaletteItems = tilesetPaletteItems;

                    paletteItemWidth = 
                        dpToPx(context, 48);
                    
                    paletteItemHeight = 
                        dpToPx(context, 48);
                    
                } else if (id == 1) {
                    activePaletteItems = eventPaletteItems;
                    paletteItemWidth = 
                        dpToPx(context, 128);
                    
                    paletteItemHeight = 
                        dpToPx(context, 48);
                }
                
                tilePaletteAdapter.notifyDataSetChanged();
                updateNumColumns();
            }

            @Override
            public void onNothingSelected(
                final AdapterView<?> parent
            ) {
                // do nothing
            }
        });
        
        this.paletteItemWidth = 
            dpToPx(context, 48);
        
        this.paletteItemHeight = 
            dpToPx(context, 48);
        
        this.tilePaletteAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return activePaletteItems.size();
            }

            @Override
            public Object getItem(int position) {
                return activePaletteItems.get(position);
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
                    activePaletteItems.get(position);

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
                
                tileView.setLayoutParams(new GridView.LayoutParams(paletteItemWidth, paletteItemHeight));
                tileView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                tileView.setPadding(4, 4, 4, 4);
                
                tileView.setImageDrawable(tileData.getPreview());
                
                return tileView;
            }
        };

        
        tileGridView.setAdapter(tilePaletteAdapter);
        
        this.tileGridView.setNumColumns(4);
        setWidth(4 * paletteItemWidth);
        
        tileGridView.setOnTouchListener(new OnTouchListener() {
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
                        if (dx > paletteItemWidth && dx > dy) {
                            paletteSwipeDetected = true;

                            focusPosition = tileGridView.getFirstVisiblePosition();
                            
                        } else if (dy > paletteItemWidth && dy > dx) {
                            paletteSwipeImpossible = true;
                        }
                        
                    } else if (paletteSwipeImpossible == true) {
                        return false;
                        
                    }
                    
                    if (paletteSwipeDetected) {
                        final float resizeDx = (e.getX() - lastX);
                        
                        resizeWidth((int) resizeDx);
                        
                        updateNumColumns();

                        lastX = e.getX();
                        lastY = e.getY();
                        
                        tileGridView.setSelection(focusPosition);
                        
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

        tileGridView.setOnItemClickListener(new OnItemClickListener() {
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
                
                final PaletteItem paletteItem = 
                    (PaletteItem) adapter.getItem(position);
                
                eventBus.post(new PaletteItemSelectedEvent(paletteItem));   
            }
        });
    }

    private int dpToPx(final Context context, int dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp);
    }

    private void updateNumColumns() {
        final int newColumnCount = Math.max(Math.min(TilePalettePresenter.this.tileGridView.getWidth() / paletteItemWidth, 16), 1);
        TilePalettePresenter.this.tileGridView.setNumColumns(newColumnCount);
    }
}
