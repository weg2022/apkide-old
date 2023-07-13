package com.apkide.ui.views.editor;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.OverScroller;

public class Editor extends View {
    public Editor(Context context) {
        super(context);
        initView();
    }

    public Editor(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public Editor(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private final Rect myRect = new Rect(0, 0, 0, 0);
    private final TextPaint myPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private Typeface myTypeface = Typeface.MONOSPACE;
    private int myFontSize = 18;
    private String myFontFeature;
    private final Paint.FontMetrics myFontMetrics = new Paint.FontMetrics();
    private float myFontTop;
    private float myFontBottom;
    private float myFontHeight;
    private float mySpaceCharWidth;
    private int myTabSize = 4;
    private boolean myUseSpaces;
    private int myIndentationSize = 4;

    private int myMaxColumn;
    private int myMaxColumnWidthLine;
    private float myMaxWidth;
    private float myMaxHeight;
    private float mySidebarWidth;
    private float myRightSpaceWidth;
    private float myBottomSpaceWidth;
    private OverScroller myOverScroller;
    private Model myModel;

    private void initView() {

    }
}
