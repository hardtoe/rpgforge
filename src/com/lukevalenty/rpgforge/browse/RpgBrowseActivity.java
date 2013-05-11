package com.lukevalenty.rpgforge.browse;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.inject.Inject;
import com.lukevalenty.rpgforge.DialogUtil;
import com.lukevalenty.rpgforge.DialogUtil.StringPromptListener;
import com.lukevalenty.rpgforge.GameView;
import com.lukevalenty.rpgforge.R;
import com.lukevalenty.rpgforge.RpgForgeApplication;
import com.lukevalenty.rpgforge.data.BuiltinData;
import com.lukevalenty.rpgforge.data.RpgDatabase;
import com.lukevalenty.rpgforge.data.RpgDatabaseLoader;
import com.lukevalenty.rpgforge.data.TileData;
import com.lukevalenty.rpgforge.data.RpgList;
import com.lukevalenty.rpgforge.edit.MapEditActivity;

import de.greenrobot.event.EventBus;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RpgBrowseActivity extends RoboFragmentActivity {
    public static final String TAG = RpgBrowseActivity.class.getName();

    private static final String RPG_LIST_FILENAME = "rpgListFile";
    
    @InjectView(R.id.rpgListView) ListView rpgListView;
    @Inject private EventBus eventBus;

    private Kryo kryo;
    private RpgList rpgList;
    
    // FIXME: this should be moved to RpgForgeApplication
            
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_activity);

        kryo = new Kryo();
        kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
        
        rpgList = loadRpgList();

        
        final BaseAdapter rpgListAdapter = 
            new BaseAdapter() {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView label = new TextView(RpgBrowseActivity.this);
                    label.setGravity(Gravity.CENTER);
                    label.setTextSize(24);
                    label.setPadding(16, 24, 16, 24);
                    label.setText(rpgList.get(position));
                    return label;
                }
                
                @Override
                public long getItemId(int position) {
                    return System.identityHashCode(getItem(position));
                }
                
                @Override
                public Object getItem(int position) {
                    return rpgList.get(position);
                }
                
                @Override
                public int getCount() {
                    return rpgList.getSize();
                }
            };
        
        Button newProjectButton =
            new Button(this);
        
        newProjectButton.setText("New Project");
        
        newProjectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                DialogUtil.showStringPrompt(RpgBrowseActivity.this, "Project Name", "Create Project", "Cancel", 
                    new StringPromptListener() {
                        @Override
                        public void onAccept(final String newProjectName) {
                            rpgList.add(newProjectName);
                            rpgListAdapter.notifyDataSetChanged();
                        }
                    });
            }
        });
        
        rpgListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(
                final AdapterView<?> parent, 
                final View view, 
                final int position,
                final long row
            ) {
                final String projectName = rpgList.get(position);
                Intent intent = new Intent(RpgBrowseActivity.this, MapEditActivity.class);
                intent.putExtra("PROJECT_NAME", projectName);
                startActivity(intent);
                overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
            }
        });
        
        rpgListView.addFooterView(newProjectButton);       
        rpgListView.setAdapter(rpgListAdapter);
    }


    @Override 
    public void onResume() {
        super.onResume();
    }
    
    @Override 
    public void onPause() {
        super.onPause();
    }
    
    @Override 
    protected void onSaveInstanceState(
        final Bundle outState
    ) {
        super.onSaveInstanceState(outState);
        saveRpgList(rpgList);
    }

    public RpgList loadRpgList() {
        try {
            final Input input = 
                new Input(openFileInput(RPG_LIST_FILENAME));
            
            final RpgList list =  
                kryo.readObject(input, RpgList.class);
            
            input.close();
            return list;
            
        } catch (final FileNotFoundException e) {
            return new RpgList();
            
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void saveRpgList(
        final RpgList rpgList
    ) {
        try {
            final Output output = 
                new Output(this.openFileOutput(RPG_LIST_FILENAME, Context.MODE_PRIVATE));
            
            kryo.writeObject(output, rpgList);
            
            output.close();
            
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
