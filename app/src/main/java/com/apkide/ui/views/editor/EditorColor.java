package com.apkide.ui.views.editor;

import android.graphics.Color;

public class EditorColor {
    private final int color;

    public EditorColor(int a, int r, int g, int b) {
        this(Color.argb(a, r, g, b));
    }

    public EditorColor(int r, int g, int b) {
        this(Color.rgb(r, g, b));
    }

    public EditorColor(int color) {
        this.color = color;
    }

    public int alpha() {
        return Color.alpha(color);
    }

    public int red() {
        return Color.red(color);
    }

    public int green() {
        return Color.green(color);
    }

    public int blue() {
        return Color.blue(color);
    }

    public int value() {
        return color;
    }
}
