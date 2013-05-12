package com.lukevalenty.rpgforge;

import roboguice.inject.ContextScope;

import android.app.Application;

import com.google.inject.Binder;
import com.google.inject.Module;

import de.greenrobot.event.EventBus;

public class RpgForgeModule implements Module {
    private Application application;

    public RpgForgeModule(final Application application) {
        this.application = application;
    }

    @Override
    public void configure(final Binder binder) {
        binder.bind(EventBus.class).toInstance(EventBus.getDefault());
    }
}
