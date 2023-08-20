package com.apkide.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;

import com.apkide.common.color.Color;
import com.apkide.ui.R;
import com.apkide.ui.views.editor.EditorView;
import com.apkide.ui.views.editor.SelectionListener;
import com.apkide.ui.views.editor.Theme;

import java.util.Timer;
import java.util.TimerTask;

public class CodeEditTextScrollView extends ScrollView {
    private boolean myVisible;
    private DragHandle myMiddleHandle;
    private DragHandle myLeftHandle;
    private DragHandle myRightHandle;
    private DragHandle[] myHandles;
    private boolean mySelectionVisible;
    
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
    
    private void initView() {
        
        myMiddleHandle = new DragHandle(getIcon(R.drawable.text_select_handle_middle),
                getIcon(R.drawable.text_select_handle_middle),
                0.5f, -0.2f, 100000L) {
            //0.5f, -0.2f, 3000L) {
            
            @Override
            protected int getColumn() {
                return getEditorView().getCaretColumn();
            }
            
            @Override
            protected int getLine() {
                return getEditorView().getCaretLine();
            }
            
            @Override
            protected void onClick() {
            
            }
            
            @Override
            protected void onDraggedTo(int line, int column) {
                getEditorView().moveCaret(line, column);
            }
        };
        
        myLeftHandle = new DragHandle(
                getIcon(R.drawable.text_select_handle_left),
                getIcon(R.drawable.text_select_handle_left),
                0.8f, -0.2f, 10000000L) {
            @Override
            protected int getColumn() {
                return getEditorView().getSelectionAnchorColumn();
            }
            
            @Override
            protected int getLine() {
                return getEditorView().getSelectionAnchorLine();
            }
            
            @Override
            protected void onClick() {
            
            }
            
            @Override
            protected void onDraggedTo(int line, int column) {
                getEditorView().selection(line, column,
                        getEditorView().getSelectionPointLine(),
                        getEditorView().getSelectionPointColumn());
            }
        };
        
        myRightHandle = new DragHandle(
                getIcon(R.drawable.text_select_handle_right),
                getIcon(R.drawable.text_select_handle_right),
                0.2f, -0.2f, 10000000L) {
            @Override
            protected int getColumn() {
                return getEditorView().getSelectionPointColumn();
            }
            
            @Override
            protected int getLine() {
                return getEditorView().getSelectionPointLine();
            }
            
            @Override
            protected void onClick() {
            
            }
            
            @Override
            protected void onDraggedTo(int line, int column) {
                getEditorView().selection(
                        getEditorView().getSelectionAnchorLine(),
                        getEditorView().getSelectionAnchorColumn(),
                        line, column);
            }
        };
        
        myHandles = new DragHandle[]{
                myMiddleHandle,
                myLeftHandle,
                myRightHandle
        };
    }
    
    public void updateCaret() {
        if (!myMiddleHandle.isVisible() || getEditorView().getCaretLine() == myMiddleHandle.getLine()) {
            return;
        }
        getEditorView().moveCaret(myMiddleHandle.getLine(), myMiddleHandle.getColumn());
    }
    
    public void showDragHandle() {
        if (getEditorView().isSelectionVisibility()) {
            makeVisible(true);
        } else {
            makeVisible(false);
        }
    }
    
    protected HorizontalScrollView getHorizontalScrollView() {
        return (HorizontalScrollView) getChildAt(0);
    }
    
    protected CodeEditText getCodeEditText() {
        return (CodeEditText) getHorizontalScrollView().getChildAt(0);
    }
    
    protected EditorView getEditorView() {
        return getCodeEditText().getEditorView();
    }
    
    private float getFontHeight() {
        return getEditorView().getFontHeight();
    }
    
    private float getSpaceCharWidth() {
        return getEditorView().getSpaceCharWidth();
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        getEditorView().addCaretListener((console, caretLine, caretColumn, isTyping) -> {
            scrollToCaretVisible();
            hide();
            showDragHandle();
        });
        
        getEditorView().addSelectionListener(new SelectionListener() {
            @Override
            public void selectionUpdate(EditorView view) {
                hide();
                showDragHandle();
            }
            
            @Override
            public void selectionChanged(EditorView view, boolean selectionVisibility) {
                hide();
                showDragHandle();
            }
            
        });
        scrollToVisible(getEditorView().getCaretLine(), getEditorView().getCaretColumn(), 1, 1, 1, 1);
        hide();
    }
    
    @Override
    public void draw(Canvas canvas) {
        int save = canvas.save();
        super.draw(canvas);
        canvas.restoreToCount(save);
        for (DragHandle handle : myHandles) {
            handle.draw(canvas);
        }
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
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("state", super.onSaveInstanceState());
        bundle.putInt("scrollX", getScrollColumnX());
        bundle.putInt("scrollY", getScrollLineY());
        return bundle;
    }
    
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            super.onRestoreInstanceState(((Bundle) state).getParcelable("state"));
            postDelayed(() -> {
                scrollTo(getScrollColumnX(), ((Bundle) state).getInt("scrollY"));
                getHorizontalScrollView().scrollTo(((Bundle) state).getInt("scrollX"), getHorizontalScrollView().getScrollY());
            }, 100L);
            return;
        }
        super.onRestoreInstanceState(state);
    }
    
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        for (DragHandle handle : myHandles) {
            if (handle.onTouchEvent(ev)) {
                return true;
            }
        }
        return super.onTouchEvent(ev);
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        for (DragHandle handle : myHandles) {
            if (handle.onInterceptTouchEvent(ev)) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }
    
    
    public void selection(int startLine, int startColumn, int endLine, int endColumn) {
        mySelectionVisible = true;
        if (myMiddleHandle.isVisible() && startLine != endLine && startColumn != endColumn) {
            makeVisible(true);
        }
        getEditorView().selection(startLine, startColumn, endLine, endColumn);
        if (myMiddleHandle.isVisible() || myLeftHandle.isVisible()) {
            invalidate();
        }
        mySelectionVisible = false;
    }
    
    public void hide() {
        if (!myVisible || !mySelectionVisible) {
            myMiddleHandle.hide();
            myLeftHandle.hide();
            myRightHandle.hide();
        }
    }
    
    public void makeVisible(boolean select) {
        if (!select) {
            myMiddleHandle.show();
            myLeftHandle.hide();
            myRightHandle.hide();
        } else {
            myMiddleHandle.hide();
            myLeftHandle.show();
            myRightHandle.show();
        }
    }
    
    public void destroy() {
        for (DragHandle handle : myHandles) {
            handle.onDestroy();
        }
    }
    
    public boolean isTouchEventInsideHandle(MotionEvent event) {
        return myMiddleHandle.isTouchEventInsideHandle(event)
                || myLeftHandle.isTouchEventInsideHandle(event)
                || myRightHandle.isTouchEventInsideHandle(event);
    }
    
    @SuppressLint("UseCompatLoadingForDrawables")
    private Drawable getIcon(int id) {
        return getContext().getDrawable(id);
    }
    
    public int getScrollLineY() {
        return getScrollY();
    }
    
    private int getScrollColumnX() {
        return getHorizontalScrollView().getScrollX();
    }
    
    public int getFirstVisibleLine() {
        return (int) (getScrollLineY() / getFontHeight());
    }
    
    
    public boolean scrollToCaretVisible() {
        return scrollToVisible(
                getEditorView().getCaretLine(),
                getEditorView().getCaretColumn(),
                1, 1, 1, 1);
    }
    
    public boolean scrollToVisible(int line, int column, int lineCount, int paddingRight, int paddingBottom, int paddingLeft) {
        boolean columnXChanged = false;
        boolean lineYChanged = false;
        
        int firstVisibleLine = (getFirstVisibleLine() + lineCount) - 1;
        int vLine = ((getFirstVisibleLine() + ((int) (getMeasuredHeight() / getFontHeight()))) - paddingBottom) + 1;
        if (line < firstVisibleLine) {
            lineYChanged = true;
            firstVisibleLine = (line - lineCount) + 1;
        }
        if (line > vLine) {
            lineYChanged = true;
            firstVisibleLine = (line - (int) (getMeasuredHeight() / getFontHeight())) + paddingBottom;
        }
        
        float sidebarX = getEditorView().getSidebarX();
        float lineX = getEditorView().computeLocationX(line, column);
        float scrollX = getScrollColumnX();
        float left = lineX - (paddingLeft * getSpaceCharWidth()) - sidebarX;
        
        if (left < getScrollColumnX()) {
            scrollX = left;
            columnXChanged = true;
        }
        
        float right = lineX + (paddingRight * getSpaceCharWidth());
        if (right > getScrollColumnX() + getMeasuredWidth()) {
            scrollX = right - getMeasuredWidth();
            columnXChanged = true;
        }
        
        if (lineYChanged) {
            if (firstVisibleLine < 0)
                firstVisibleLine = 0;
            
            scrollTo(getScrollX(), (int) ((firstVisibleLine * getFontHeight())));
        }
        
        
        if (columnXChanged)
            getHorizontalScrollView().scrollTo((int) scrollX, getScrollLineY());
        
        if (line <= 0)
            scrollTo(getScrollX(), 0);
        
        invalidate();
        return columnXChanged || lineYChanged;
    }
    
    public abstract class DragHandle {
        private final int handleCenterX;
        private final int handleCenterY;
        private final int handleHeight;
        private final int handleWidth;
        private final Drawable icon;
        private final Drawable iconDown;
        private final Timer removeTimer = new Timer();
        private final long timeout;
        private int dragDX;
        private int dragDY;
        private boolean dragIsActive;
        private boolean isDown;
        private boolean isVisible;
        private TimerTask removeTimerTask;
        
        public DragHandle(@NonNull Drawable icon, @NonNull Drawable iconDown, float x, float y, long timeout) {
            this.icon = icon;
            this.iconDown = iconDown;
            this.handleHeight = icon.getIntrinsicHeight();
            this.handleWidth = icon.getIntrinsicWidth();
            this.handleCenterX = (int) (this.handleWidth * x);
            this.handleCenterY = (int) (this.handleHeight * y);
            this.timeout = timeout;
        }
        
        protected abstract int getColumn();
        
        protected abstract int getLine();
        
        protected abstract void onClick();
        
        protected abstract void onDraggedTo(int line, int column);
        
        public void onDestroy() {
            removeTimer.cancel();
        }
        
        public boolean isVisible() {
            return isVisible;
        }
        
        public void show() {
            isVisible = true;
            invalidate();
            scheduleRemoveCaretHandle();
        }
        
        public void hide() {
            if (isVisible) {
                isVisible = false;
                invalidate();
                if (removeTimerTask != null)
                    removeTimerTask.cancel();
            }
        }
        
        public boolean isTouchEventInsideHandle(MotionEvent e) {
            if (isVisible) {
                EditorView editorView = getEditorView();
                int line = getLine();
                return getHandleRect(
                        (int) (editorView.computeLocationX(line, getColumn()) - getScrollColumnX()),
                        (int) (editorView.computeLocationY(line) - getScrollLineY()))
                        .contains((int) e.getX(0), (int) e.getY(0));
            }
            return false;
        }
        
        private Rect getHandleRect(int x, int y) {
            float fontHeight = getFontHeight();
            return new Rect(x - handleCenterX,
                    (int) (((y + fontHeight) - handleCenterY) - fontHeight),
                    (x - handleCenterX) + handleWidth,
                    (int) ((((y + fontHeight) + handleHeight) - handleCenterY)
                            - fontHeight));
        }
        
        @NonNull
        private Rect getHandleRectInWindow() {
            EditorView editorView = getEditorView();
            return getHandleRect(
                    (int) (editorView.computeLocationX(getLine(), getColumn()) -
                            getScrollColumnX()),
                    (int) (editorView.computeLocationY(getLine())));
        }
        
        public boolean onInterceptTouchEvent(@NonNull MotionEvent e) {
            if (isVisible && e.getActionMasked() == 0 && e.getPointerCount() == 1) {
                int x = (int) e.getX(0);
                int y = (int) e.getY(0);
                EditorView editorView = getEditorView();
                int line = getLine();
                int column = getColumn();
                int lineY = (int) (editorView.computeLocationY(line) - getScrollLineY());
                int lineX = (int) (editorView.computeLocationX(line, column) - getScrollColumnX());
                if (getHandleRect(lineX, lineY).contains(x, y)) {
                    dragIsActive = true;
                    isDown = true;
                    if (this.removeTimerTask != null) {
                        this.removeTimerTask.cancel();
                    }
                    dragDX = x - (lineX + ((int) getSpaceCharWidth()));
                    dragDY = y - ((((int) getFontHeight()) / 2) + lineY);
                    invalidate();
                }
            }
            return this.dragIsActive;
        }
        
        public boolean onTouchEvent(@NonNull MotionEvent e) {
            if (dragIsActive) {
                if (e.getAction() == 1) {
                    dragIsActive = false;
                    scheduleRemoveCaretHandle();
                    invalidate();
                    if (isDown) {
                        onClick();
                        isDown = false;
                    }
                }
                if (e.getAction() == 2) {
                    int x = (int) e.getX(0);
                    int y = (int) e.getY(0);
                    int scrollX = getScrollColumnX() + (x - dragDX);
                    int scrollLineY = getScrollLineY() + (y - dragDY);
                    int line = getEditorView().computeLine(scrollLineY);
                    int column = getEditorView().computeColumn(line, scrollX);
                    if (line == getLine() && column == getColumn()) {
                        return true;
                    }
                    myVisible = true;
                    onDraggedTo(line, column);
                    myVisible = false;
                    invalidate();
                    scrollToVisible(line, column, 1, 5, getScrollPaddingBottom(), 5);
                    return true;
                }
                return true;
            }
            return false;
        }
        
        private int getScrollPaddingBottom() {
            int paddingBottom = (int) (this.handleHeight / getFontHeight());
            int height = (int) (getMeasuredHeight() / getFontHeight());
            if (paddingBottom > height - 4) {
                paddingBottom = height - 4;
            }
            return paddingBottom + 2;
        }
        
        private void scheduleRemoveCaretHandle() {
            if (removeTimerTask != null)
                removeTimerTask.cancel();
            
            removeTimerTask = new TimerTask() {
                @Override
                public void run() {
                    post(() -> {
                        dragIsActive = false;
                        isVisible = false;
                        invalidate();
                    });
                }
            };
            removeTimer.schedule(removeTimerTask, timeout);
        }
        
        public void draw(Canvas canvas) {
            if (isVisible) {
                Rect handleRectInWindow = getHandleRectInWindow();
                Color color = getEditorView().getTheme().getColor(Theme.Colors.DragHandleColor);
                if (isDown) {
                    if (color != null && color.value != 0)
                        iconDown.setTint(color.value);
                    iconDown.setBounds(handleRectInWindow);
                    iconDown.draw(canvas);
                    return;
                }
                if (color != null && color.value != 0)
                    icon.setTint(color.value);
                icon.setBounds(handleRectInWindow);
                icon.draw(canvas);
            }
        }
    }
}