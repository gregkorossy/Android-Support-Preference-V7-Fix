package com.takisoft.preferencex.simplemenu;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.os.Build;
import android.util.Property;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class SimpleMenuBoundsProperty extends Property<PropertyHolder, Rect> {

    public static final Property<PropertyHolder, Rect> BOUNDS;

    static {
        BOUNDS = new SimpleMenuBoundsProperty("bounds");
    }

    public SimpleMenuBoundsProperty(String name) {
        super(Rect.class, name);
    }

    @Override
    public Rect get(PropertyHolder holder) {
        return holder.getBounds();
    }

    @Override
    public void set(PropertyHolder holder, Rect value) {
        holder.setBounds(value);
    }
}
