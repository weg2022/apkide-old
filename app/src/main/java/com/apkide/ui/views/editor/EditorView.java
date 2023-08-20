package com.apkide.ui.views.editor;

import static java.lang.Math.max;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

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
    private EditorInputConnection myInputConnection;
    
    private void initView() {
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
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                moveCaretUp();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                moveCaretDown();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                moveCaretLeft();
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                moveCaretRight();
                break;
            case KeyEvent.KEYCODE_ENTER:
                insertLineBreak();
                break;
            case KeyEvent.KEYCODE_TAB:
                insertTab();
                break;
            case KeyEvent.KEYCODE_SPACE:
                insertChar(' ');
                break;
            default:
                super.onKeyDown(keyCode, event);
        }
        return true;
    }
    
    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }
    
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        if (!isEditable()) {
            return super.onCreateInputConnection(outAttrs);
        }
        
        if (myInputConnection == null)
            myInputConnection = new EditorInputConnection(this);
        
        outAttrs.inputType = EditorInfo.TYPE_CLASS_TEXT |
                        EditorInfo.TYPE_TEXT_VARIATION_NORMAL |
                        EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS |
                        EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE |
                        EditorInfo.TYPE_TEXT_FLAG_IME_MULTI_LINE;
        
        outAttrs.imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI |
                EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        
        return myInputConnection;
    }
}
