package com.lukevalenty.rpgforge.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

public class RpgDatabaseLoader {
    private final Kryo kryo;

    public RpgDatabaseLoader() {
        kryo = new Kryo();
        kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
    }
    
    public RpgDatabase load(
        final Context context, 
        final String rpgDatabaseFile
    ) {
        try {
            final Input input = 
                new Input(context.openFileInput(rpgDatabaseFile));
            
            final RpgDatabase rpgDatabase =  
                kryo.readObject(input, RpgDatabase.class);
            
            rpgDatabase.load(context);
            
            input.close();
            return rpgDatabase;
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void save(
        final Context context, 
        final String rpgDatabaseFile, 
        final RpgDatabase rpgDatabase
    ) {
        try {
            final Output output = 
                new Output(context.openFileOutput(rpgDatabaseFile, Context.MODE_PRIVATE));
            
            kryo.writeObject(output, rpgDatabase);
            
            output.close();
            
            /*
            final FileOutputStream thumbnailOutput = 
                context.openFileOutput(rpgDatabaseFile + ".png", Context.MODE_PRIVATE);
            
            final Bitmap mapBitmap = 
                rpgDatabase.getMaps().getFirst().createBitmap();
            
            final float height = 
                mapBitmap.getHeight();
            
            final float width = 
                mapBitmap.getWidth();
            
            float scale;
            
            if (height > width) {
                scale = height / 128f;
                
            } else {
                scale = width / 128f;
            }
            
            final Bitmap scaledMapBitmap =
                Bitmap.createScaledBitmap(mapBitmap, (int) (scale * width), (int) (scale * height), false);
            
            scaledMapBitmap.compress(CompressFormat.PNG, 0, thumbnailOutput);
            */
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void save(
            final Context context, 
            final File dbFile, 
            final RpgDatabase rpgDatabase
        ) {
            try {
                final Output output = 
                    new Output(new FileOutputStream(dbFile));
                
                kryo.writeObject(output, rpgDatabase);
                
                output.close();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    public RpgDatabase load(
        final Context context, 
        final File dbFile
    ) {
        try {
            final Input input = 
                new Input(new FileInputStream(dbFile));
            
            final RpgDatabase rpgDatabase =  
                kryo.readObject(input, RpgDatabase.class);
            
            rpgDatabase.load(context);
            
            input.close();
            return rpgDatabase;
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
