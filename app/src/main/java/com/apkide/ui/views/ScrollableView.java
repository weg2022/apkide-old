package com.apkide.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ScrollableView extends View implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    public ScrollableView(Context context) {
        super(context);
        initView();
    }
    
    public ScrollableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    
    public ScrollableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }
    
    public interface ScrollingListener{
    
    }
    
    private OverScroller myOverScroller;
    
    private void initView() {
        myOverScroller = new OverScroller(getContext());
    }
    
    public void setOverScroller(OverScroller overScroller) {
        myOverScroller = overScroller;
    }
    
    public OverScroller getOverScroller() {
        return myOverScroller;
    }
    
    public boolean isScrollable() {
        return true;
    }
    
    public int getComponentWidth(){
        return getWidth();
    }
    
    public int getComponentHeight(){
        return getHeight();
    }
    
    public int getMinScrollX(){
        return 0;
    }
    
    public int getMaxScrollX(){
        return getWidth();
    }
    
    public int getMinScrollY(){
        return 0;
    }
    
    public int getMaxScrollY(){
        return getHeight();
    }
    
    @Override
    public void computeScroll() {
        if (isScrollable() && myOverScroller != null &&
                myOverScroller.computeScrollOffset()) {
            scrollTo(myOverScroller.getCurrX(), myOverScroller.getCurrY());
        }
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
    public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        
        
        int dx = (int) Math.abs(distanceX);
        int dy = (int) Math.abs(distanceY);
        if (dx > dy) {
        
        } else if (dy > dx) {
        
        } else {
        
        }
        return false;
    }
    
    
    @Override
    public void onLongPress(@NonNull MotionEvent e) {
    
    }
    
    @Override
    public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
      
      
        return false;
    }
}
