package com.lukevalenty.rpgforge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.inject.Inject;
import com.lukevalenty.rpgforge.data.RpgDatabase;
import com.lukevalenty.rpgforge.data.RpgDatabaseLoader;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import roboguice.RoboGuice;

// FIXME: this needs to be made into a singleton or the RpgDatabase needs to be moved elsewhere (maybe into the RpgDatabaseLoader)
public class RpgForgeApplication extends Application {
    private static File activeDatabaseFilename = null;
    private static RpgDatabaseLoader loader = new RpgDatabaseLoader();
    
    private static RpgDatabase db;
    

    
    public static RpgDatabase getDb() {
        return db;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        RoboGuice.setBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE, 
            RoboGuice.newDefaultRoboModule(this), new RpgForgeModule(this));
    }

    public static File getDbFile() {
        return activeDatabaseFilename;
    }

    public static void save(Context context) {
        loader.save(context, activeDatabaseFilename, db);
    }

    public static void load(
        final Context context,
        final File dbFile
    ) {
        activeDatabaseFilename = dbFile;
        
        RpgDatabase rpgDatabase;

        // clean up old memory
        db = null;
        
        if (!dbFile.exists()) {
            copyAsset(context, "Template.rpg", dbFile);
        }

        rpgDatabase = loader.load(context, dbFile);
          
        db = rpgDatabase;
    }
    
    private static void copyAsset(
        final Context context, 
        final String src,
        final File dst
    ) {
        final AssetManager assetManager = context.getAssets();

        InputStream in = null;
        OutputStream out = null;
        
        try {
            in = assetManager.open(src);
            out = new FileOutputStream(dst);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            
        } catch(IOException e) {
            Log.e("tag", "Failed to copy asset file: " + src, e);
        }       

    }
    
    private static void copyFile(
        final InputStream in, 
        final OutputStream out
    ) throws 
        IOException 
    {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
          out.write(buffer, 0, read);
        }
    }
}
