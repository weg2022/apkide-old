package com.apkide.editor;

import android.text.TextPaint;

public class EditorPaint extends TextPaint {
    public int tabAdvanceInt;
    public float tabAdvance;
    public int spaceAdvanceInt;
    public float spaceAdvance;
    private int tabSize = 4;
    private final FontMetrics fontMetrics = new FontMetrics();
    private final FontMetricsInt fontMetricsInt = new FontMetricsInt();
    private final PaintMetrics tempMetrics = new PaintMetrics();
    private final PaintMetrics metrics = new PaintMetrics();

    public EditorPaint() {
        super(ANTI_ALIAS_FLAG);
        invalidate();
    }

    public void set(EditorPaint p) {
        super.set(p);
        this.tabSize = p.tabSize;
        invalidate();
    }

    @Override
    public void setTextSize(float textSize) {
        super.setTextSize(textSize);
        invalidate();
    }

    public void setTabSize(int tabSize) {
        this.tabSize = tabSize;
        invalidate();
    }

    public int getTabSize() {
        return tabSize;
    }

    private void invalidate() {
        getFontMetrics(fontMetrics);
        getFontMetricsInt(fontMetricsInt);
        metrics.top = fontMetrics.top;
        metrics.ascent = fontMetrics.ascent;
        metrics.descent = fontMetrics.descent;
        metrics.bottom = fontMetrics.bottom;
        metrics.leading = fontMetrics.leading;
        metrics.topInt = fontMetricsInt.top;
        metrics.ascentInt = fontMetricsInt.ascent;
        metrics.descentInt = fontMetricsInt.descent;
        metrics.bottomInt = fontMetricsInt.bottom;
        metrics.leadingInt = fontMetricsInt.leading;
        spaceAdvance = measureText(" ");
        spaceAdvanceInt = (int) spaceAdvance;
        tabAdvanceInt = spaceAdvanceInt * tabSize;
        tabAdvance = spaceAdvance * tabSize;
    }

    public int getHeightInt() {
        return metrics.bottomInt - metrics.topInt;
    }

    public float getHeight() {
        return metrics.bottom - metrics.top;
    }

    public PaintMetrics getMetrics() {
        tempMetrics.set(metrics);
        return tempMetrics;
    }
}
