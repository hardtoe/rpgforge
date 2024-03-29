package com.lukevalenty.rpgforge.browse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.inject.Inject;
import com.lukevalenty.rpgforge.BaseActivity;
import com.lukevalenty.rpgforge.DialogUtil;
import com.lukevalenty.rpgforge.DialogUtil.StringPromptListener;
import com.lukevalenty.rpgforge.R;
import com.lukevalenty.rpgforge.data.RpgList;

import de.greenrobot.event.EventBus;

import roboguice.inject.InjectView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class BrowseGamesActivity extends BaseActivity {
    public static final String TAG = BrowseGamesActivity.class.getName();

    private static final String RPG_LIST_FILENAME = "rpgListFile";
    
    @InjectView(R.id.rpgListView) ListView rpgListView;
    @Inject private EventBus eventBus;

    private final File externalDir = 
        new File(Environment.getExternalStorageDirectory(), "RpgForge");

    private File[] rpgDirList;
    
            
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_games_layout);

        convertToExternalStorage();

        rpgDirList = 
            externalDir.listFiles();
        
        final BaseAdapter rpgListAdapter = 
            new BaseAdapter() {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView label = new TextView(BrowseGamesActivity.this);
                    label.setGravity(Gravity.CENTER);
                    label.setTextSize(24);
                    label.setPadding(16, 24, 16, 24);
                    label.setText(rpgDirList[position].getName());
                    label.setBackgroundColor(Color.TRANSPARENT);
                    return label;
                }
                
                @Override
                public long getItemId(int position) {
                    return System.identityHashCode(getItem(position));
                }
                
                @Override
                public Object getItem(int position) {
                    return rpgDirList[position];
                }
                
                @Override
                public int getCount() {
                    return rpgDirList.length;
                }
            };
        
        Button newProjectButton =
            new Button(this);
        
        newProjectButton.setText("New Project");
        
        newProjectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                DialogUtil.showStringPrompt(BrowseGamesActivity.this, "Project Name", "Create Project", "Cancel", 
                    new StringPromptListener() {
                        @Override
                        public void onAccept(final String newProjectName) {
                            //rpgList.add(newProjectName);
                            new File(externalDir, newProjectName).mkdir();
                            
                            rpgDirList = 
                                externalDir.listFiles();
                            
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
                final String projectName = rpgDirList[position].getName();
                //Intent intent = new Intent(BrowseGamesActivity.this, GameOverviewActivity.class);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                //intent.putExtra("PROJECT_NAME", projectName);
                intent.setData(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "RpgForge" + File.separator + projectName + File.separator + projectName + ".rpg")));
                startActivity(intent);
                overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
            }
        });
        
        rpgListView.addFooterView(newProjectButton);       
        rpgListView.setAdapter(rpgListAdapter);
    }


    private void convertToExternalStorage() {
        
        if (!externalDir.exists()) {
            externalDir.mkdir();
            
            
            final RpgList rpgList = loadRpgList();
            
            for (int i = 0; i < rpgList.getSize(); i++) {
                final String gameName = 
                    rpgList.get(i);
                
                final File internalGameFile = 
                    this.getFileStreamPath(Base64.encodeToString(gameName.getBytes(), Base64.DEFAULT));
                
                final File externalGameDir = 
                    new File(externalDir, gameName.replaceAll("[\\n\\t]", ""));
                
                externalGameDir.mkdir();
                
                final File externalGameFile =
                    new File(externalGameDir, gameName + ".rpg");
                
                try {
                    copyFile(internalGameFile, externalGameFile);
                    
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    public void copyFile(
        final File sourceFile, 
        final File destFile
    ) throws 
        IOException 
    {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;
        
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
            
        } finally {
            if (source != null) {
                source.close();
            }
            
            if (destination != null) {
                destination.close();
            }
        }
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
        //saveRpgList(rpgList);
    }

    public RpgList loadRpgList() {
        final Kryo kryo = new Kryo();
        kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
        
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
}
