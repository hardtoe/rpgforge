package com.lukevalenty.rpgforge.edit;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lukevalenty.rpgforge.RpgForgeApplication;
import com.lukevalenty.rpgforge.data.MapData;

import de.greenrobot.event.EventBus;

public class MapSelectionPresenter {
    

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public MapSelectionPresenter(
        final Context context, 
        final EventBus eventBus, 
        final ActionBar actionBar
    ) {
        final BaseAdapter mapSelectionAdapter = new BaseAdapter() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView label = new TextView(context);
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

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            actionBar.setListNavigationCallbacks(mapSelectionAdapter, mapSelectionListener);         
        }      
    }
}
