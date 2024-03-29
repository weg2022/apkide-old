package com.apkide.ui.views.editor;

import static java.lang.Math.log10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.apkide.common.SyncRunner;
import com.apkide.common.color.Color;
import com.apkide.common.text.TextModel;
import com.apkide.common.text.TextStyle;

public class Console extends View implements TextModel.TextChangeListener, Theme.ThemeListener {
    public Console(Context context) {
        super(context);
        initView();
    }
    
    public Console(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    
    public Console(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }
    
    
    private Typeface myTypeface = Typeface.MONOSPACE;
    private int myFontSize = 16;
    private final TextPaint myPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Paint.FontMetrics myFontMetrics = new Paint.FontMetrics();
    private float myFontTop;
    private float myFontBottom;
    private float myFontHeight;
    private float myNumberCharWidth;
    private float myTabCharWidth;
    private float mySpaceCharWidth;
    
    private int myTabSize = 4;
    private boolean myInsertTabsAsSpaces;
    
    private boolean myOverwriteMode;
    private Caret myCaret;
    private boolean myCaretVisibility;
    private int myCaretLine = 0;
    private int myCaretColumn = 0;
    private boolean mySelectionVisibility;
    private int mySelectionAnchorLine = 0;
    private int mySelectionAnchorColumn = 0;
    private int mySelectionPointLine = 0;
    private int mySelectionPointColumn = 0;
    private int myFirstSelectedLine = 0;
    private int myFirstSelectedColumn = 0;
    private int myLastSelectedLine = 0;
    private int myLastSelectedColumn = 0;
    private int myMaxColumn = 0;
    private int myMaxColumnLine = 0;
    private float mySidebarX = 0;
    private SyncRunner myComputeMaxColumnRunner;
    private SyncRunner myLayoutUpdateRunner;
    
    private Model myModel;
    private Theme myTheme = new Theme();
    
    private void initView() {
        myCaret = new Caret(this);
        
        myComputeMaxColumnRunner = new SyncRunner(this::computeMaxColumn);
        myLayoutUpdateRunner = new SyncRunner(() -> {
            if (isShown()) {
                requestLayout();
                invalidate();
            }
        });
    }
    
    public void applyColorScheme(@NonNull Theme.ColorScheme scheme) {
        myTheme.applyScheme(scheme);
    }
    
    public void setTheme(@NonNull Theme theme) {
        if (myTheme != null) {
            myTheme.removeListener(this);
        }
        myTheme = theme;
        myTheme.addListener(this);
    }
    
    @NonNull
    public Theme getTheme() {
        return myTheme;
    }
    
    public void setModel(@NonNull Model model) {
        if (myModel != null)
            myModel.removeTextModelListener(this);
        
        myModel = model;
        myModel.addTextModelListener(this);
        configurePaint();
        computeMaxColumn();
        computeSidebar();
        redraw();
        myComputeMaxColumnRunner.run();
        myLayoutUpdateRunner.run();
    }
    
    public Model getModel() {
        return myModel;
    }
    
    public int getLineCount() {
        return myModel.getLineCount();
    }
    
    private void computeSidebar() {
        float padding = (float) ((myNumberCharWidth * log10(myModel.getLineCount() + 4.0d))) + getSidebarPaddingLeft();
        mySidebarX = padding + (mySpaceCharWidth * 2.0f);
    }
    
    protected float getSidebarPaddingLeft() {
        return 0;
    }
    
    
    public void setTabSize(int tabSize) {
        if (myTabSize != tabSize) {
            myTabSize = tabSize;
            myTabCharWidth = myTabSize * mySpaceCharWidth;
            invalidate();
            myComputeMaxColumnRunner.run();
            myLayoutUpdateRunner.run();
        }
    }
    
    public int getTabSize() {
        return myTabSize;
    }
    
    public void setInsertTabsAsSpaces(boolean insertTabsAsSpaces) {
        myInsertTabsAsSpaces = insertTabsAsSpaces;
    }
    
    public boolean isInsertTabsAsSpaces() {
        return myInsertTabsAsSpaces;
    }
    
    
    public void setOverwriteMode(boolean overwriteMode) {
        if (myOverwriteMode != overwriteMode) {
            myOverwriteMode = overwriteMode;
            fireOverwriteMode(myOverwriteMode);
        }
    }
    
    protected void fireOverwriteMode(boolean overwriteMode) {
    
    }
    
    public boolean isOverwriteMode() {
        return myOverwriteMode;
    }
    
    protected void configurePaint() {
        myPaint.setTypeface(myTypeface);
        myPaint.setTextSize(getDeviceFontSize());
        myPaint.getFontMetrics(myFontMetrics);
        myFontTop = -myFontMetrics.top;
        myFontBottom = myFontMetrics.bottom;
        myFontHeight = ((-myFontMetrics.top) + myFontMetrics.bottom + myFontMetrics.leading);
        mySpaceCharWidth = myPaint.measureText("M");
        myNumberCharWidth = myPaint.measureText("8");
        myTabCharWidth = mySpaceCharWidth * myTabSize;
    }
    
    public void setTypeface(@NonNull Typeface typeface) {
        if (myTypeface != typeface) {
            myTypeface = typeface;
            configurePaint();
            invalidate();
            myLayoutUpdateRunner.run();
        }
    }
    
    @NonNull
    public Typeface getTypeface() {
        return myTypeface;
    }
    
    public void setFontSize(int fontSize) {
        if (myFontSize != fontSize) {
            myFontSize = fontSize;
            configurePaint();
            invalidate();
            myLayoutUpdateRunner.run();
        }
    }
    
    public int getFontSize() {
        return myFontSize;
    }
    
    public float getDeviceFontSize() {
        return myFontSize * getContext().getResources().getDisplayMetrics().density;
    }
    
    public float getFontTop() {
        return myFontTop;
    }
    
    public float getFontBottom() {
        return myFontBottom;
    }
    
    public float getFontHeight() {
        return myFontHeight;
    }
    
    public float getSpaceCharWidth() {
        return mySpaceCharWidth;
    }
    
    public float getTabCharWidth() {
        return myTabCharWidth;
    }
    
    protected void computeMaxColumn() {
        int lineCount = myModel.getLineCount();
        int maxColumnLine = 0;
        int maxColumn = 0;
        for (int line = 0; line < lineCount; line++) {
            int lineMaxColumn = getLineMaxColumn(line);
            if (lineMaxColumn > maxColumn) {
                maxColumn = lineMaxColumn;
                maxColumnLine = line;
            }
        }
        myMaxColumn = maxColumn;
        myMaxColumnLine = maxColumnLine;
    }
    
    public int getLineMaxColumn(int line) {
        int length = myModel.getLineLength(line);
        int maxColumn = 0;
        for (int column = 0; column < length; column++) {
            maxColumn += myModel.getChar(line, column) == '\t' ? myTabSize - (maxColumn % myTabSize) : 1;
        }
        return maxColumn;
    }
    
    public int getCaretLine() {
        return myCaretLine;
    }
    
    public int getCaretColumn() {
        return myCaretColumn;
    }
    
    
    public void setCaretVisibility(boolean caretVisibility) {
        if (myCaretVisibility != caretVisibility) {
            if (caretVisibility)
                myCaret.schedule();
            else
                myCaret.cancel();
            
            myCaretVisibility = caretVisibility;
            invalidate();
        }
    }
    
    public boolean isCaretVisibility() {
        return myCaretVisibility;
    }
    
    public void setCaretBlinks(boolean caretBlinks) {
        myCaret.setBlinks(caretBlinks);
    }
    
    public boolean isCaretBlinks() {
        return myCaret.isBlinks();
    }
    
    public boolean isCaretShowing() {
        return myCaret.isShowing();
    }
    
    public void moveCaret(int line, int column) {
        moveCaret(line, column, false);
    }
    
    private void moveCaret(int line, int column, boolean isTyping) {
        if (line < 0) line = 0;
        if (line >= myModel.getLineCount())
            line = myModel.getLineCount() - 1;
        
        if (column < 0) column = 0;
        if (column > myModel.getLineLength(line))
            column = myModel.getLineLength(line);
        
        if (myCaretLine == line && myCaretColumn == column) return;
        
        myCaretLine = line;
        myCaretColumn = column;
        fireCaretMoved(myCaretLine, myCaretColumn, isTyping);
        if (myCaretVisibility) myCaret.schedule();
        
        invalidate();
    }
    
    protected void fireCaretMoved(int caretLine, int caretColumn, boolean isTyping) {
    
    }
    
    
    public void setSelectionVisibility(boolean selectionVisibility) {
        if (mySelectionVisibility != selectionVisibility) {
            if (!selectionVisibility ||
                    computeDiffCount(mySelectionAnchorLine,
                            mySelectionAnchorColumn, mySelectionPointLine,
                            mySelectionPointColumn) != 0) {
                mySelectionVisibility = selectionVisibility;
                fireSelectionChanged(selectionVisibility);
                invalidate();
            }
        }
    }
    
    protected void fireSelectionChanged(boolean selectionVisibility) {
    
    }
    
    public boolean isSelectionVisibility() {
        return mySelectionVisibility;
    }
    
    
    public void selection(int startLine, int startColumn, int endLine, int endColumn) {
        if (startLine >= myModel.getLineCount())
            startLine = myModel.getLineCount() - 1;
        
        if (startColumn > myModel.getLineLength(startLine))
            startColumn = myModel.getLineLength(startLine);
        
        if (endLine >= myModel.getLineCount())
            endLine = myModel.getLineCount() - 1;
        
        if (endColumn > myModel.getLineLength(endLine))
            endColumn = myModel.getLineLength(endLine);
        
        moveCaret(startLine, startColumn);
        setSelectionVisibility(true);
        setSelectionAnchor(startLine, startColumn);
        setSelectionPoint(endLine, endColumn);
    }
    
    public void setSelectionAnchor(int line, int column) {
        if (line < 0) line = 0;
        
        if (line > myModel.getLineCount()) line = myModel.getLineCount() - 1;
        
        if (column < 0) column = 0;
        
        if (column > myModel.getLineLength(line)) column = myModel.getLineLength(line);
        
        mySelectionAnchorLine = line;
        mySelectionAnchorColumn = column;
        updateSelection();
    }
    
    public void setSelectionPoint(int line, int column) {
        if (line < 0) line = 0;
        
        if (line > myModel.getLineCount()) line = myModel.getLineCount() - 1;
        
        if (column < 0) column = 0;
        
        if (column > myModel.getLineLength(line)) column = myModel.getLineLength(line);
        
        mySelectionPointLine = line;
        mySelectionPointColumn = column;
        updateSelection();
    }
    
    protected void updateSelection() {
        int count = computeDiffCount(mySelectionAnchorLine,
                mySelectionAnchorColumn,
                mySelectionPointLine,
                mySelectionPointColumn);
        if (count < 0) {
            setSelectionVisibility(true);
            updateSelection(mySelectionAnchorLine,
                    mySelectionAnchorColumn,
                    mySelectionPointLine,
                    mySelectionPointColumn);
        } else if (count > 0) {
            setSelectionVisibility(true);
            updateSelection(mySelectionPointLine,
                    mySelectionPointColumn,
                    mySelectionAnchorLine,
                    mySelectionAnchorColumn);
        } else {
            myFirstSelectedLine = myLastSelectedLine = myCaretLine;
            myFirstSelectedColumn = myLastSelectedColumn = myCaretColumn;
            setSelectionVisibility(false);
            invalidate();
        }
    }
    
    protected void updateSelection(int startLine, int startColumn, int endLine, int endColumn) {
        if (startLine == myFirstSelectedLine && startColumn == myFirstSelectedColumn
                && myLastSelectedLine == endLine && myLastSelectedColumn == endColumn) {
            return;
        }
        
        myFirstSelectedLine = startLine;
        myFirstSelectedColumn = startColumn;
        myLastSelectedLine = endLine;
        myLastSelectedColumn = endColumn;
        fireSelectionUpdate();
        invalidate();
    }
    
    protected void fireSelectionUpdate() {
    
    }
    
    public int getSelectionAnchorLine() {
        return mySelectionAnchorLine;
    }
    
    public int getSelectionAnchorColumn() {
        return mySelectionAnchorColumn;
    }
    
    public int getSelectionPointLine() {
        return mySelectionPointLine;
    }
    
    public int getSelectionPointColumn() {
        return mySelectionPointColumn;
    }
    
    
    public int getFirstSelectedLine() {
        return myFirstSelectedLine;
    }
    
    public int getFirstSelectedColumn() {
        return myFirstSelectedColumn;
    }
    
    public int getLastSelectedLine() {
        return myLastSelectedLine;
    }
    
    public int getLastSelectedColumn() {
        return myLastSelectedColumn;
    }
    
    protected int computeDiffCount(int firstLine, int firstColumn, int lastLine, int lastColumn) {
        return firstLine == lastLine ? firstColumn - lastColumn : firstLine - lastLine;
    }
    
    @Override
    public void prepareInsert(@NonNull TextModel model, int line, int column, @NonNull String newText) {
    
    }
    
    @Override
    public void insertUpdate(@NonNull TextModel model, int startLine, int startColumn, int endLine, int endColumn) {
        computeSidebar();
        invalidate();
      
        moveCaret(endLine,endColumn==-1?0:endColumn);
        
        setSelectionVisibility(false);
        
        if (startLine == endLine) {
            int maxColumn = getLineMaxColumn(startLine);
            if (myModel.getLineCount() == 1) {
                myMaxColumn = maxColumn;
                return;
            } else if (myComputeMaxColumnRunner.isRunning() || maxColumn <= myMaxColumn) {
                return;
            } else {
                myMaxColumnLine = startLine;
                myMaxColumn = maxColumn;
                myLayoutUpdateRunner.run();
                return;
            }
        }
        if (endLine - startLine > myModel.getLineCount()) {
            myComputeMaxColumnRunner.run();
        } else if (!myComputeMaxColumnRunner.isRunning()) {
            for (int i = startLine; i <= endLine; i++) {
                int maxColumn = getLineMaxColumn(i);
                if (maxColumn > myMaxColumn) {
                    myMaxColumn = maxColumn;
                    myMaxColumnLine = i;
                    break;
                }
            }
        }
        myLayoutUpdateRunner.run();
    }
    
    @Override
    public void prepareRemove(@NonNull TextModel model, int startLine, int startColumn, int endLine, int endColumn) {
    
    }
    
    @Override
    public void removeUpdate(@NonNull TextModel model, int startLine, int startColumn, int endLine, int endColumn) {
        computeSidebar();
        
        invalidate();
        moveCaret(startLine,startColumn==-1?0:startColumn);
        
        setSelectionVisibility(false);
        
        if (startLine == endLine) {
            if (startLine == myMaxColumnLine) {
                if (myModel.getLineCount() == 1) {
                    myMaxColumn = getLineMaxColumn(startLine);
                    return;
                }
                myComputeMaxColumnRunner.run();
                myLayoutUpdateRunner.run();
                return;
            }
            return;
        }
        invalidate();
        myComputeMaxColumnRunner.run();
        myLayoutUpdateRunner.run();
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /*setMeasuredDimension(
                (int) (((myMaxColumn + 1) * mySpaceCharWidth) + mySidebarX),
                myModel == null ? 0 :
                        (int) ((myModel.getLineCount() + 1) * myFontHeight));*/
    }
    
    
    public int getComponentWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }
    
    public int getComponentHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }
    
    public int getMaxWidth() {
        return (int) (((myMaxColumn + 1) * mySpaceCharWidth) + mySidebarX);
    }
    
    public int getMaxHeight() {
        return myModel == null ? 0 : (int) ((myModel.getLineCount() + 1) * myFontHeight);
    }
    
    
    public float getSidebarX() {
        return mySidebarX;
    }
    
    @NonNull
    public Point computeLocation(int line, int column) {
        if (line < 0) line = 0;
        
        if (line >= getModel().getLineCount())
            line = getModel().getLineCount() - 1;
        
        if (column > getModel().getLineLength(line))
            column = getModel().getLineLength(line);
        
        float y = computeLocationY(line);
        float x = computeLocationX(line, column);
        return new Point((int) x, (int) y);
    }
    
    @NonNull
    public Point computePosition(float x, float y) {
        int line = computeLine(y);
        int column = computeColumn(line, x);
        return new Point(column, line);
    }
    
    public int computeLine(float y) {
        return (int) (y / myFontHeight);
    }
    
    public float computeLocationY(int line) {
        return (line + 1) * myFontHeight;
    }
    
    public int computeColumn(float x, float y) {
        int line = computeLine(y);
        return computeColumn(line, x);
    }
    
    public int computeColumn(int line, float x) {
        if (line < 0) line = 0;
        if (line >= getModel().getLineCount())
            line = getModel().getLineCount() - 1;
        int maxColumn = getModel().getLineLength(line);
        float columnX = mySidebarX;
        if (maxColumn == 0) return 0;
        int column = 0;
        float[] buffer = Buffers.floats(maxColumn);
        getLineWidths(line, 0, maxColumn, buffer);
        for (int i = 0; i < maxColumn; i++) {
            if (columnX >= x) break;
            columnX += buffer[i];
            column++;
        }
        Buffers.recycle(buffer);
        return column;
    }
    
    public float computeLocationX(int line, int column) {
        if (getModel() == null) {
            return 0;
        }
        if (line < 0) line = 0;
        if (line >= getModel().getLineCount())
            line = getModel().getLineCount() - 1;
        int length = getModel().getLineLength(line);
        if (column < 0) column = 0;
        if (column > length) column = length;
        float x = mySidebarX;
        if (column > 0) {
            x += measureLine(line, 0, column);
        }
        return x;
    }
    
    
    public float measureLine(int line, int startColumn, int endColumn) {
        return nativeMeasureLine(line, startColumn, endColumn);
    }
    
    public void getLineWidths(int line, int startColumn, int endColumn, float[] widths) {
        nativeGetLineWidths(line, startColumn, endColumn, widths);
    }
    
    
    protected float nativeMeasureLine(int line, int startColumn, int endColumn) {
        float w = myModel.measureLine(line, startColumn, endColumn, myPaint);
        float tabAdvance = myPaint.measureText("\t");
        for (int i = startColumn; i < endColumn; i++) {
            char c = myModel.getChar(line, i);
            if (c == '\t') {
                w -= tabAdvance;
                w += myTabCharWidth;
            }
        }
        return w;
    }
    
    protected void nativeGetLineWidths(int line, int startColumn, int endColumn, float[] widths) {
        myModel.getLineWidths(line, startColumn, endColumn, widths, myPaint);
        for (int i = startColumn; i < endColumn; i++) {
            char c = myModel.getChar(line, i);
            if (c == '\t')
                widths[i] = myTabCharWidth;
        }
    }
    
    protected float drawText(Canvas canvas, int line, int startColumn, int endColumn, float x, float y) {
        int begin = startColumn;
        for (int i = startColumn; i < endColumn; i++) {
            if (myModel.getChar(line, i) == '\t') {
                if (myPaint.getColor() != 0)
                    myModel.drawLine(line, begin, i, x, y, canvas, myPaint);
                x += measureLine(line, begin, i + 1);
                begin = i + 1;
            }
        }
        if (begin < endColumn) {
            if (myPaint.getColor() != 0)
                myModel.drawLine(line, begin, endColumn, x, y, canvas, myPaint);
            x += measureLine(line, begin, endColumn);
        }
        return x;
    }
    
    public void redraw() {
        invalidate();
    }
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        Color color = myTheme.getColor(Theme.Colors.BackgroundColor);
        if (color != null && color.value != 0)
            canvas.drawColor(color.value);
        
        
        int firstLine = computeLine(getScrollY());
         int lastLine = computeLine(canvas.getClipBounds().bottom + 1);
        if (firstLine >= getModel().getLineCount()) firstLine = getModel().getLineCount() - 1;
        
        if (lastLine >= getModel().getLineCount()) lastLine = getModel().getLineCount() - 1;
        
        if (firstLine < 0) firstLine = 0;
        
        if (firstLine <= lastLine) redrawLines(canvas, firstLine, lastLine);
    }
    
    protected void redrawLines(Canvas canvas, int startLine, int endLine) {
        float x = mySidebarX;
        float y = computeLocationY(startLine);
        for (int i = startLine; i <= endLine; i++) {
            int length = myModel.getLineLength(i);
            redrawCaretLine(canvas, i, length, x, y);
            redrawLineNumber(canvas, i, length, x, y);
            redrawSelection(canvas, i, length, x, y);
            redrawFindMatch(canvas, i, length, x, y);
            redrawText(canvas, i, length, x, y);
            redrawColor(canvas, i, length, x, y);
            redrawTyping(canvas, i, length, x, y);
            redrawHyperlink(canvas, i, length, x, y);
            redrawProblem(canvas, i, length, x, y);
            redrawCaret(canvas, i, length, x, y);
            y += myFontHeight;
        }
    }
    
    protected void redrawLineNumber(Canvas canvas, int line, int length, float x, float y) {
        Color color = myTheme.getColor(Theme.Colors.LineNumberColor);
        if (line == myCaretLine) {
            Color color1 = myTheme.getColor(Theme.Colors.CaretLineNumberColor);
            if (color1 != null)
                color = color1;
        }
        if (color != null && color.value != 0) {
            myPaint.setColor(color.value);
            canvas.drawText(Integer.toString(line + 1), 0, y, myPaint);
        }
    }
    
    protected void redrawCaretLine(Canvas canvas, int line, int length, float x, float y) {
        Color color = myTheme.getColor(Theme.Colors.CaretLineColor);
        if (color != null && color.value != 0 && !mySelectionVisibility && myCaretLine == line) {
            myPaint.setColor(color.value);
            canvas.drawRect(x, y - myFontTop, canvas.getClipBounds().right, y + myFontBottom, myPaint);
        }
    }
    
    protected void redrawCaret(Canvas canvas, int line, int length, float x, float y) {
        Color color = myTheme.getColor(Theme.Colors.CaretColor);
        if (color != null && color.value != 0 && !mySelectionVisibility && myCaretLine == line) {
            if (!myCaretVisibility) return;
            boolean showing = true;
            if (myCaret.isBlinks()) showing = myCaret.isShowing();
            
            if (showing) {
                myPaint.setColor(color.value);
                float caretX = x;
                float density = getContext().getResources().getDisplayMetrics().density;
                if (myCaretColumn > 0) caretX += measureLine(line, 0, myCaretColumn);
                
                canvas.drawRect(caretX,
                        y - myFontTop,
                        caretX + density,
                        y + myFontBottom, myPaint);
            }
        }
    }
    
    protected void redrawSelection(Canvas canvas, int line, int length, float x, float y) {
        if (!mySelectionVisibility || line < myFirstSelectedLine || line > myLastSelectedLine)
            return;
        Color color = myTheme.getColor(Theme.Colors.SelectionColor);
        if (color == null || color.value == 0) return;
        myPaint.setColor(color.value);
        float spaceWidth = mySpaceCharWidth;
        if (myFirstSelectedLine == myLastSelectedLine) {
            float firstX = length == 0 ? x + spaceWidth :
                    x + measureLine(line, 0, myFirstSelectedColumn);
            float lastX = length == 0 ? x + spaceWidth :
                    x + measureLine(line, 0, myLastSelectedColumn);
            canvas.drawRect(firstX, y - myFontTop, lastX, y + myFontBottom, myPaint);
        } else {
            if (line == myFirstSelectedLine) {
                float width = measureLine(line, 0, length);
                float cX = length == 0 ? x + spaceWidth :
                        x + measureLine(line, 0, myFirstSelectedColumn);
                canvas.drawRect(cX, y - myFontTop,
                        x + width, y + myFontBottom, myPaint);
            } else if (line == myLastSelectedLine) {
                float cX = length == 0 ? x + spaceWidth :
                        x + measureLine(line, 0, myLastSelectedColumn);
                canvas.drawRect(x, y - myFontTop, cX, y + myFontBottom, myPaint);
            } else {
                float width = length == 0 ? spaceWidth :
                        measureLine(line, 0, length);
                canvas.drawRect(x, y - myFontTop, x + width, y + myFontBottom, myPaint);
            }
        }
    }
    
    protected void redrawFindMatch(Canvas canvas, int line, int maxColumn, float x, float y) {
        if (maxColumn == 0) return;
        
    }
    
    protected void redrawProblem(Canvas canvas, int line, int maxColumn, float x, float y) {
        if (maxColumn == 0) return;
        
    }
    
    protected void redrawHyperlink(Canvas canvas, int line, int maxColumn, float x, float y) {
        if (maxColumn == 0) return;
        
        int fontColor = 0xff212121;
        Color color = myTheme.getColor(Theme.Colors.FontColor);
        if (color != null) {
            fontColor = color.value;
        }
        
    }
    
    protected void redrawColor(Canvas canvas, int line, int maxColumn, float x, float y) {
        if (maxColumn == 0) return;
        
    }
    
    protected void redrawTyping(Canvas canvas, int line, int maxColumn, float x, float y) {
        if (maxColumn == 0) return;
        
        int fontColor = 0xff212121;
        Color color = myTheme.getColor(Theme.Colors.FontColor);
        if (color != null) {
            fontColor = color.value;
        }
        
    }
    
    protected void redrawText(Canvas canvas, int line, int maxColumn, float x, float y) {
        if (maxColumn == 0) return;
        
        int fontColor = 0xff212121;
        Color color = myTheme.getColor(Theme.Colors.FontColor);
        if (color != null) {
            fontColor = color.value;
        }
        int lastStyle = getModel().getStyle(line, 0);
        TextStyle textStyle = myTheme.getTextStyle(lastStyle);
        if (textStyle != null)
            applyFontStyle(textStyle.fontStyle);
        else
            applyFontStyle(Typeface.NORMAL);
        
        int column = 0;
        for (int i = 0; i < maxColumn; i++) {
            int style = getModel().getStyle(line, i);
            if (style != lastStyle) {
                if (textStyle != null) {
                    if (textStyle.backgroundColor != null && textStyle.backgroundColor.value != 0) {
                        myPaint.setColor(textStyle.backgroundColor.value);
                        float advance = nativeMeasureLine(line, column, i);
                        canvas.drawRect(x, y - myFontTop, x + advance, y + myFontBottom, myPaint);
                    }
                    applyFontColor(textStyle, fontColor);
                } else {
                    myPaint.setColor(fontColor);
                }
                
                x = drawText(canvas, line, column, i, x, y);
                column = i;
                lastStyle = style;
                textStyle = myTheme.getTextStyle(lastStyle);
                if (textStyle != null)
                    applyFontStyle(textStyle.fontStyle);
                else applyFontStyle(Typeface.NORMAL);
            }
        }
        
        if (column < maxColumn) {
            if (textStyle != null) {
                applyFontColor(textStyle, fontColor);
                applyFontStyle(textStyle.fontStyle);
            } else {
                applyFontStyle(Typeface.NORMAL);
                myPaint.setColor(fontColor);
            }
            drawText(canvas, line, column, maxColumn, x, y);
        }
        
        applyFontStyle(Typeface.NORMAL);
        myPaint.setColor(fontColor);
    }
    
    protected void applyFontColor(TextStyle style, int fontColor) {
        if (style.fontColor != null && style.fontColor.value != 0) {
            myPaint.setColor(style.fontColor.value);
        } else {
            myPaint.setColor(fontColor);
        }
    }
    
    protected void applyFontStyle(int fontStyle) {
        switch (fontStyle) {
            case Typeface.BOLD:
                myPaint.setFakeBoldText(true);
                break;
            case Typeface.BOLD_ITALIC:
                myPaint.setFakeBoldText(true);
                myPaint.setTextSkewX(-0.25f);
                break;
            case Typeface.ITALIC:
                myPaint.setTextSkewX(-0.25f);
                break;
            default:
                myPaint.setFakeBoldText(false);
                myPaint.setTextSkewX(0);
                break;
        }
    }
    
    @Override
    public void themeColorSchemeChanged(@NonNull Theme theme) {
        invalidate();
    }
    
    @Override
    public void themeColorChanged(int colorId) {
        invalidate();
    }
    
    @Override
    public void themeStyleChanged(int styleId) {
        invalidate();
    }
}
