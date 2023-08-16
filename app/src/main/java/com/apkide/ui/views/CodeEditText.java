package com.apkide.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.common.ActionRunnable;
import com.apkide.common.KeyStroke;
import com.apkide.ui.views.editor.EditorView;

import java.util.Hashtable;

public class CodeEditText extends ViewGroup {
    public CodeEditText(Context context) {
        super(context);
        initView();
    }
    
    public CodeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    
    public CodeEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }
    
    private final Hashtable<KeyStroke, ActionRunnable> myKeymap = new Hashtable<>();
    
    private void initView() {
        removeAllViews();
        addView(new EditorView(getContext()));
        setModel(new CodeEditTextModel());
    }
    
    public void setModel(@NonNull CodeEditTextModel model) {
        getEditorView().setModel(model);
    }
    
    @NonNull
    public CodeEditTextModel getModel() {
        return (CodeEditTextModel) getEditorView().getModel();
    }
    
    
    public void boundKeyAction(@NonNull KeyStroke key, @Nullable ActionRunnable runnable) {
        myKeymap.put(key, runnable);
    }
    
    public void unboundKeyAction(@NonNull KeyStroke key) {
        myKeymap.remove(key);
    }
    
    public boolean isBoundKey(@NonNull KeyStroke key){
        return myKeymap.contains(key);
    }
    
    @Nullable
    public ActionRunnable getKeyAction(@NonNull KeyStroke key) {
        if (myKeymap.contains(key)) {
            return myKeymap.get(key);
        }
        return null;
    }
    
    @NonNull
    protected EditorView getEditorView() {
        return (EditorView) getChildAt(0);
    }
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        getChildAt(0).layout(0, 0, r - l, b - t);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View view = getChildAt(0);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(view.getMeasuredWidth(), view.getMeasuredHeight());
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        
        
        return super.onKeyUp(keyCode, event);
    }
    
    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }
    
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return super.onCreateInputConnection(outAttrs);
    }
}
