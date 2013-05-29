package com.lukevalenty.rpgforge.data;

import android.graphics.drawable.Drawable;

import com.lukevalenty.rpgforge.edit.PaletteItem;
import com.lukevalenty.rpgforge.engine.GameObject;

public abstract class EventData implements PaletteItem {
    private transient Drawable preview;

    @Override
    public Drawable getPreview() {
        if (preview == null) {
            preview = createPreview();
        }
        
        return preview;
    }

    protected abstract Drawable createPreview();

    public abstract GameObject getGameObject();

    public abstract EventData create();
}
