package com.apkide.ui.views;


import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.getSize;
import static android.view.View.MeasureSpec.makeMeasureSpec;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class SplitLayout extends ViewGroup {
    
    public SplitLayout(Context context) {
        super(context);
        initView();
    }
    
    public SplitLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    
    public SplitLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }
    
    private boolean isSplit;
    private OnSplitChangeListener listener;
    
    private void initView() {
    
    }
    
    public void updateChildVisibilities() {
        boolean visible = isSplit;
        getBottomView().setVisibility(visible ? VISIBLE : GONE);
        getSeparatorView().setVisibility(visible ? VISIBLE : GONE);
    }
    
    public void closeSplit() {
        closeSplit(null);
    }
    
    public View getBottomView() {
        return getChildAt(2);
    }
    
    public View getSeparatorView() {
        return getChildAt(1);
    }
    
    public View getTopView() {
        return getChildAt(0);
    }
    
    public boolean isSplit() {
        return isSplit;
    }
    
    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        updateChildVisibilities();
    }
    
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View topView = getTopView();
        View bottomView = getBottomView();
        View separatorView = getSeparatorView();
        int h = getSize(heightMeasureSpec);
        int w = getSize(widthMeasureSpec);
        if (bottomView.getVisibility() != GONE) {
            bottomView.measure(
                    widthMeasureSpec,
                    makeMeasureSpec(getDividerPosition(h), EXACTLY));
            topView.measure(
                    widthMeasureSpec,
                    makeMeasureSpec(h - bottomView.getMeasuredHeight(), EXACTLY));
            separatorView.measure(widthMeasureSpec, -2);
        } else {
            topView.measure(widthMeasureSpec, heightMeasureSpec);
        }
        setMeasuredDimension(w, h);
    }
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View topView = getTopView();
        View bottomView = getBottomView();
        View separatorView = getSeparatorView();
        int w = r - l;
        int h = b - t;
        if (bottomView.getVisibility() != GONE) {
            int measuredHeight = topView.getMeasuredHeight();
            int separatorHeight = separatorView.getMeasuredHeight();
            topView.layout(0, 0, w, measuredHeight);
            int mHeight = separatorHeight + measuredHeight;
            separatorView.layout(0, measuredHeight, w, mHeight);
            bottomView.layout(0, mHeight, w, h);
            return;
        }
        topView.layout(0, 0, w, h);
    }
    
    public void setOnSplitChangeListener(OnSplitChangeListener listener) {
        this.listener = listener;
    }
    
    
    public void toggleSplit(Runnable closeRun) {
        if (isSplit())
            closeSplit(closeRun);
        else
            openSplit();
    }
    
    private int getDividerPosition(int height) {
        float verticalSplitRatio = 0.5f;
        return (int) (verticalSplitRatio * height);
    }
    
    public void closeSplit(Runnable closeRun) {
        if (isSplit) {
            isSplit = false;
            updateChildVisibilities();
            if (listener != null)
                listener.onSplitChanged(isSplit);
            
            if (closeRun != null)
                closeRun.run();
        }
    }
    
    public void openSplit() {
        if (!isSplit) {
            isSplit = true;
            updateChildVisibilities();
            if (listener != null)
                listener.onSplitChanged(isSplit);
        }
    }
    
    
    public interface OnSplitChangeListener {
        
        void onSplitChanged(boolean isSplit);
    }
}
