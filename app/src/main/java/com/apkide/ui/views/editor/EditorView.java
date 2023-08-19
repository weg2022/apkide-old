package com.apkide.ui.views.editor;

import static java.lang.Math.max;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;


public class EditorView extends Editor {
    public EditorView(Context context) {
        super(context);
        initView();
    }
    
    public EditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    
    public EditorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }
    
    private final List<CaretListener> myCaretListeners = new ArrayList<>(1);
    private final List<SelectionListener> mySelectionListeners = new ArrayList<>(1);
    private WindowManager windowManager;
    
    private void initView() {
        setModel(new EditorModel());
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        setEditable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setCaretVisibility(true);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width;
        int height;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            width = windowManager.getCurrentWindowMetrics().getBounds().width();
            height = windowManager.getCurrentWindowMetrics().getBounds().height();
        } else {
            width = windowManager.getDefaultDisplay().getWidth();
            height = windowManager.getDefaultDisplay().getHeight();
        }
        if (isUseMaxLayout())
            setMeasuredDimension(getMeasuredWidth() + width, getMeasuredHeight() + height);
        else
            setMeasuredDimension(max(getMeasuredWidth(), width), max(getMeasuredHeight(), height));
    }
    
    public boolean isUseMaxLayout() {
        return false;
    }
    
    public void addCaretListener(@NonNull CaretListener listener) {
        if (!myCaretListeners.contains(listener)) {
            myCaretListeners.add(listener);
        }
    }
    
    public void removeCaretListener(@NonNull CaretListener listener) {
        myCaretListeners.remove(listener);
    }
    
    @Override
    protected void fireCaretMoved(int caretLine, int caretColumn, boolean isTyping) {
        super.fireCaretMoved(caretLine, caretColumn, isTyping);
        for (CaretListener listener : myCaretListeners) {
            listener.caretUpdate(this, caretLine, caretColumn, isTyping);
        }
    }
    
    public void addSelectionListener(@NonNull SelectionListener listener) {
        if (!mySelectionListeners.contains(listener))
            mySelectionListeners.add(listener);
    }
    
    public void removeSelectionListener(@NonNull SelectionListener listener) {
        mySelectionListeners.remove(listener);
    }
    
    @Override
    protected void fireSelectionUpdate() {
        super.fireSelectionUpdate();
        for (SelectionListener listener : mySelectionListeners) {
            listener.selectionUpdate(this);
        }
    }
    
    @Override
    protected void fireSelectionChanged(boolean selectionVisibility) {
        super.fireSelectionChanged(selectionVisibility);
        for (SelectionListener listener : mySelectionListeners) {
            listener.selectionChanged(this, selectionVisibility);
        }
    }
}
