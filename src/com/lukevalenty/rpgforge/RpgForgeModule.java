package com.lukevalenty.rpgforge;

import android.app.Application;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import de.greenrobot.event.EventBus;

public class RpgForgeModule implements Module {
    private Application application;
    private GoogleAnalytics mGaInstance;

    public RpgForgeModule(final Application application) {
        this.application = application;
        this.mGaInstance = GoogleAnalytics.getInstance(application);
    }

    @Override
    public void configure(final Binder binder) {
        binder.bind(EventBus.class).toInstance(EventBus.getDefault());
    }
    
    @Provides @Singleton
    public Tracker createTracker() {
        return mGaInstance.getTracker("UA-41090423-1");
    }
}
