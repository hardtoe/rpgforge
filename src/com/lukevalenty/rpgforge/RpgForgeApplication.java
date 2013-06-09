package com.lukevalenty.rpgforge;

import java.io.File;

import com.google.inject.Inject;
import com.lukevalenty.rpgforge.data.BuiltinData;
import com.lukevalenty.rpgforge.data.RpgDatabase;
import com.lukevalenty.rpgforge.data.RpgDatabaseLoader;

import android.app.Application;
import android.content.Context;
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
        
        if (dbFile.exists()) {
            rpgDatabase = loader.load(context, dbFile);
            
        } else {
            rpgDatabase = BuiltinData.createNewDatabase(context);
        }
          
        db = rpgDatabase;
    }
}
