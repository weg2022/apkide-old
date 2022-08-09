package com.apkide.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class QuickKeyListView extends RecyclerView {
    public QuickKeyListView(Context context) {
        super(context);
        initView();
    }

    public QuickKeyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public QuickKeyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private static final String TAG = "QuickKeyListView";

    private KeyEventListener keyEventListener;
    private void initView() {

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }


    protected boolean callSuperKeyUp(int keyCode,KeyEvent event){
        return super.onKeyUp(keyCode,event);
    }

    protected boolean callSuperKeyDown(int keyCode,KeyEvent event){
        return super.onKeyDown(keyCode,event);
    }

    public void setKeyEventListener(@NonNull KeyEventListener keyEventListener) {
        this.keyEventListener = keyEventListener;
    }

    public interface KeyEventListener {
        boolean keyUp(int keyCode, KeyEvent event);

        boolean keyDown(int keyCode, KeyEvent event);
    }
}
