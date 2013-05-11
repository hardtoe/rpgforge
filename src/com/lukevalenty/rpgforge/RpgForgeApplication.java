package com.lukevalenty.rpgforge;

import com.lukevalenty.rpgforge.data.RpgDatabase;

import android.app.Application;
import roboguice.RoboGuice;

// FIXME: this needs to be made into a singleton or the RpgDatabase needs to be moved elsewhere (maybe into the RpgDatabaseLoader)
public class RpgForgeApplication extends Application {
    private static RpgDatabase db;
    
    public static void setDb(final RpgDatabase db) {
        RpgForgeApplication.db = db;
    }
    
    public static RpgDatabase getDb() {
        return db;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        RoboGuice.setBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE, 
            RoboGuice.newDefaultRoboModule(this), new RpgForgeModule());
    }
}
