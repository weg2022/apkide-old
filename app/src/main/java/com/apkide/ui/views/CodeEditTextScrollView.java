package com.apkide.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;

public class CodeEditTextScrollView extends ScrollView implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener {
    public CodeEditTextScrollView(Context context) {
        super(context);
        initView();
    }

    public CodeEditTextScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CodeEditTextScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private GestureDetector myGestureDetector;
    private ScaleGestureDetector myScaleGestureDetector;

    private void initView() {
        myGestureDetector = new GestureDetector(getContext(), this);
        myGestureDetector.setOnDoubleTapListener(this);
        myScaleGestureDetector = new ScaleGestureDetector(getContext(), this);
    }

    private HorizontalScrollView getHorizontalScrollView() {
        return (HorizontalScrollView) getChildAt(0);
    }

    private CodeEditText getCodeEditText() {
        return (CodeEditText) ((ViewGroup) getChildAt(0)).getChildAt(0);
    }

    private CodeEditText.EditorView getEditorView() {
        return getCodeEditText().getEditorView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        getEditorView().redraw();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        getEditorView().redraw();
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean handle = myGestureDetector.onTouchEvent(ev) || myScaleGestureDetector.onTouchEvent(ev);
        if (handle) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean handle = myGestureDetector.onTouchEvent(ev) || myScaleGestureDetector.onTouchEvent(ev);
        if (handle) {
            return true;
        }

        return super.onTouchEvent(ev);
    }


    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        myGestureDetector.onGenericMotionEvent(event);
        return super.onGenericMotionEvent(event);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onScale(@NonNull ScaleGestureDetector detector) {
        return false;
    }

    @Override
    public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
        return false;
    }

    @Override
    public void onScaleEnd(@NonNull ScaleGestureDetector detector) {

    }
}
