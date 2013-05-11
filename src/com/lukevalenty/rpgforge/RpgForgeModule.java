package com.lukevalenty.rpgforge;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;

import de.greenrobot.event.EventBus;

public class RpgForgeModule implements Module {
    @Override
    public void configure(final Binder binder) {
        binder.bind(EventBus.class).toInstance(EventBus.getDefault());
    }
}
