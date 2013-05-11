package com.lukevalenty.rpgforge.data;

import java.io.File;

import android.content.Context;

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
            Output output = 
                new Output(context.openFileOutput(rpgDatabaseFile, Context.MODE_PRIVATE));
            
            kryo.writeObject(output, rpgDatabase);
            
            output.close();
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
