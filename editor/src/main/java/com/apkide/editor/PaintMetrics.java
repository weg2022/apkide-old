package com.apkide.editor;

public class PaintMetrics {
    public float top;
    public float ascent;
    public float descent;
    public float bottom;
    public float leading;

    public int topInt;
    public int ascentInt;
    public int descentInt;
    public int bottomInt;
    public int leadingInt;

    public PaintMetrics() {

    }

    public PaintMetrics(PaintMetrics metrics) {
        this.top = metrics.top;
        this.ascent = metrics.ascent;
        this.descent = metrics.descent;
        this.bottom = metrics.bottom;
        this.leading = metrics.leading;
        this.topInt = metrics.topInt;
        this.ascentInt = metrics.ascentInt;
        this.descentInt = metrics.descentInt;
        this.bottomInt = metrics.bottomInt;
        this.leadingInt = metrics.leadingInt;
    }

    public void set(PaintMetrics metrics) {
        this.top = metrics.top;
        this.ascent = metrics.ascent;
        this.descent = metrics.descent;
        this.bottom = metrics.bottom;
        this.leading = metrics.leading;
        this.topInt = metrics.topInt;
        this.ascentInt = metrics.ascentInt;
        this.descentInt = metrics.descentInt;
        this.bottomInt = metrics.bottomInt;
        this.leadingInt = metrics.leadingInt;
    }
}
