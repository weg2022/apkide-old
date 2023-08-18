package com.apkide.ui.views.editor;

import static java.lang.Math.log10;
import static java.lang.Math.max;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.apkide.common.Color;
import com.apkide.common.SyncRunner;
import com.apkide.common.TextModel;
import com.apkide.common.TextStyle;

public class Console extends View implements TextModel.TextModelListener, Theme.ThemeListener {
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
    
    private final Rect myRect = new Rect(0, 0, 0, 0);
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
    private int myCaretLine;
    private int myCaretColumn;
    private boolean mySelectionVisibility;
    private int mySelectionAnchorLine;
    private int mySelectionAnchorColumn;
    private int mySelectionPointLine;
    private int mySelectionPointColumn;
    private int myFirstSelectedLine;
    private int myFirstSelectedColumn;
    private int myLastSelectedLine;
    private int myLastSelectedColumn;
    private int myMaxColumn;
    private int myMaxColumnLine;
    private float mySidebarX;
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
        if (myModel != null) {
            myModel.removeTextModelListener(this);
        }
        myModel = model;
        myModel.addTextModelListener(this);
        configurePaint();
        computeMaxColumn();
        computeSidebar();
        redraw();
        myLayoutUpdateRunner.run();
    }
    
    public Model getModel() {
        return myModel;
    }
    
    private void computeSidebar() {
        float padding = (float) ((myNumberCharWidth * log10(myModel.getLineCount() + 2.0d))) + getSidebarPaddingLeft();
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
            int length = myModel.getLineLength(line);
            int lineMaxColumn = 0;
            for (int column = 0; column < length; column++) {
                lineMaxColumn += myModel.getChar(line, column) == '\t' ? myTabSize - (lineMaxColumn % myTabSize) : 1;
            }
            if (lineMaxColumn > maxColumn) {
                maxColumn = lineMaxColumn;
                maxColumnLine = line;
            }
        }
        myMaxColumn = maxColumn;
        myMaxColumnLine = maxColumnLine;
    }
    
    private int getLineMaxColumn(int line) {
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
            if (caretVisibility) myCaret.schedule();
            else myCaret.cancel();
            
            myCaretVisibility = caretVisibility;
            invalidate();
        }
    }
    
    public boolean getCaretVisibility() {
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
        if (line >= myModel.getLineCount()) line = myModel.getLineCount() - 1;
        
        if (column < 0) column = 0;
        if (column >= myModel.getLineLength(line)) column = myModel.getLineLength(line);
        
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
    
    public boolean getSelectionVisibility() {
        return mySelectionVisibility;
    }
    
    
    public void selection(int startLine, int startColumn, int endLine, int endColumn) {
        if (startLine >= myModel.getLineCount())
            startLine = myModel.getLineCount() - 1;
        
        if (startColumn >= myModel.getLineLength(startLine))
            startColumn = myModel.getLineLength(startLine);
        
        if (endLine >= myModel.getLineCount())
            endLine = myModel.getLineCount() - 1;
        
        if (endColumn >= myModel.getLineLength(endLine))
            endColumn = myModel.getLineLength(endLine);
        
        moveCaret(startLine, startColumn);
        setSelectionVisibility(true);
        setSelectionAnchor(startLine, startColumn);
        setSelectionPoint(endLine, endColumn);
    }
    
    public void setSelectionAnchor(int line, int column) {
        if (line < 0) line = 0;
        
        if (line >= myModel.getLineCount()) line = myModel.getLineCount() - 1;
        
        if (column < 0) column = 0;
        
        if (column >= myModel.getLineLength(line)) column = myModel.getLineLength(line);
        
        mySelectionAnchorLine = line;
        mySelectionAnchorColumn = column;
        updateSelection();
    }
    
    public void setSelectionPoint(int line, int column) {
        if (line < 0) line = 0;
        
        if (line >= myModel.getLineCount()) line = myModel.getLineCount() - 1;
        
        if (column < 0) column = 0;
        
        if (column >= myModel.getLineLength(line)) column = myModel.getLineLength(line);
        
        mySelectionPointLine = line;
        mySelectionPointColumn = column;
        updateSelection();
    }
    
    protected void updateSelection() {
        int count = computeDiffCount(mySelectionAnchorLine, mySelectionAnchorColumn, mySelectionPointLine, mySelectionPointColumn);
        if (count < 0) {
            setSelectionVisibility(true);
            updateSelection(mySelectionAnchorLine, mySelectionAnchorColumn, mySelectionPointLine, mySelectionPointColumn);
        } else if (count > 0) {
            setSelectionVisibility(true);
            updateSelection(mySelectionPointLine, mySelectionPointColumn, mySelectionAnchorLine, mySelectionAnchorColumn);
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
        if (startLine < myCaretLine && startLine != endLine) {
            moveCaret((myCaretLine + endLine) - startLine, myCaretColumn, true);
        } else if (startLine == myCaretLine && startColumn <= myCaretColumn) {
            if (startLine == endLine) {
                moveCaret(myCaretLine, ((myCaretColumn + endColumn) - startColumn) + 1, true);
            } else {
                moveCaret((myCaretLine + endLine) - startLine, ((myCaretColumn + endColumn) - startColumn) + 1, true);
            }
        }
        
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
        if (startLine == endLine) {
            if (endLine == myCaretLine && startColumn < myCaretColumn) {
                moveCaret(myCaretLine, max(startColumn, myCaretColumn - ((endColumn - startColumn) + 1)), true);
            }
        } else if ((myCaretLine == startLine && startColumn < myCaretColumn) || (myCaretLine > startLine && myCaretLine < endLine) || (myCaretLine == endLine && myCaretColumn < endColumn)) {
            moveCaret(startLine, startColumn, true);
        } else if (myCaretLine == endLine) {
            moveCaret(startLine, ((myCaretColumn + startColumn) - endColumn) - 1, true);
        } else if (myCaretLine > endLine) {
            moveCaret(myCaretLine - (endLine - startLine), myCaretColumn, true);
        }
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
        setMeasuredDimension(
                (int) (((myMaxColumn + 1) * mySpaceCharWidth) + mySidebarX),
                myModel == null ? 0 : (int) (myModel.getLineCount() * myFontHeight));
    }
    
    
    public float getSidebarX() {
        return mySidebarX;
    }
    
    @NonNull
    public Point computeLocation(int line, int column) {
        if (line < 0) line = 0;
        if (line >= getModel().getLineCount()) line = getModel().getLineCount() - 1;
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
        if (line >= getModel().getLineCount()) line = getModel().getLineCount() - 1;
        int length = getModel().getLineLength(line);
        float columnX = mySidebarX;
        if (length == 0) return 0;
        int column = 0;
        float[] buffer = Buffers.floats(length);
        getLineWidths(line, 0, length, buffer);
        for (int i = 0; i < length; i++) {
            if (columnX >= x) break;
            columnX += buffer[i];
            column++;
        }
        Buffers.recycle(buffer);
        return column;
    }
    
    public float computeLocationX(int line, int column) {
        if (line < 0) line = 0;
        if (line >= getModel().getLineCount()) line = getModel().getLineCount() - 1;
        int length = getModel().getLineLength(line);
        if (column < 0) column = 0;
        if (column > length) column = length;
        float x = mySidebarX;
        if (column > 0) {
            x += measureLine(line, 0, column);
        }
        return x;
    }
    
    
    public float measureLine(int line, int column, int length) {
        return nativeMeasureLine(line, column, length);
    }
    
    public void getLineWidths(int line, int column, int length, float[] widths) {
        nativeGetLineWidths(line, column, length, widths);
    }
    
    
    protected float nativeMeasureLine(int line, int column, int length) {
        int end = column + length;
        float w = myModel.measureLine(line, column, length, myPaint);
        float tabAdvance = myPaint.measureText("\t");
        for (int i = column; i < end; i++) {
            char c = myModel.getChar(line, i);
            if (c == '\t') {
                w -= tabAdvance;
                w += myTabCharWidth;
            }
        }
        return w;
    }
    
    protected void nativeGetLineWidths(int line, int column, int length, float[] widths) {
        int end = column + length;
        myModel.getLineWidths(line, column, length, widths, myPaint);
        for (int i = column; i < end; i++) {
            char c = myModel.getChar(line, i);
            if (c == '\t') widths[i] = myTabCharWidth;
        }
    }
    
    protected float drawText(Canvas canvas, int line, int column, int length, float x, float y) {
        int end = column + length;
        int begin = column;
        for (int i = column; i < end; i++) {
            if (myModel.getChar(line, i) == '\t') {
                if (myPaint.getColor() != 0)
                    myModel.drawLine(line, begin, i - begin, x, y, canvas, myPaint);
                x += measureLine(line, begin, i - begin + 1);
                begin = i + 1;
            }
        }
        if (begin < end) {
            if (myPaint.getColor() != 0)
                myModel.drawLine(line, begin, end - begin, x, y, canvas, myPaint);
            x += measureLine(line, begin, end - begin);
        }
        return x;
    }
    
    public void redraw() {
        getLocalVisibleRect(myRect);
        invalidate();
    }
    
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        Color color = myTheme.getColor(Theme.Colors.BackgroundColor);
        if (color != null &&color.value!=0) {
            canvas.drawColor(color.value);
        }
        if (myRect.isEmpty()) return;
        
        int firstLine = computeLine(myRect.top);
        int lastLine = computeLine(myRect.bottom + 1);
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
            redrawText(canvas, i, length, x, y);
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
        if (color != null && color.value != 0 && myRect.contains((int) mySidebarX, (int) y)) {
            myPaint.setColor(color.value);
            canvas.drawText(Integer.toString(line + 1), 0, y, myPaint);
        }
    }
    
    protected void redrawCaretLine(Canvas canvas, int line, int length, float x, float y) {
        Color color = myTheme.getColor(Theme.Colors.CaretLineColor);
        if (color != null && color.value != 0  && !mySelectionVisibility && myCaretLine == line) {
            myPaint.setColor(color.value);
            canvas.drawRect(x, y - myFontTop, myRect.right, y + myFontBottom, myPaint);
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
                
                canvas.drawRect(caretX, y - myFontTop, caretX + density, y + myFontBottom, myPaint);
            }
        }
    }
    
    protected void redrawSelection(Canvas canvas, int line, int length, float x, float y) {

        if (!mySelectionVisibility || line < myFirstSelectedLine || line > myLastSelectedLine)
            return;
        Color color = myTheme.getColor(Theme.Colors.SelectionColor);
        if (color==null ||color.value==0)return;
        myPaint.setColor(color.value);
        float spaceWidth = mySpaceCharWidth;
        if (myFirstSelectedLine == myLastSelectedLine) {
            float firstX = length == 0 ? x + spaceWidth : x + measureLine(line, 0, myFirstSelectedColumn);
            float lastX = length == 0 ? x + spaceWidth : x + measureLine(line, 0, myLastSelectedColumn);
            canvas.drawRect(firstX, y - myFontTop, lastX, y + myFontBottom, myPaint);
        } else {
            if (line == myFirstSelectedLine) {
                float width = measureLine(line, 0, length);
                float cX = length == 0 ? x + spaceWidth : x + measureLine(line, 0, myFirstSelectedColumn);
                canvas.drawRect(cX, y - myFontTop, x + width, y + myFontBottom, myPaint);
            } else if (line == myLastSelectedLine) {
                float cX = length == 0 ? x + spaceWidth : x + measureLine(line, 0, myLastSelectedColumn);
                canvas.drawRect(x, y - myFontTop, cX, y + myFontBottom, myPaint);
            } else {
                float width = length == 0 ? spaceWidth : measureLine(line, 0, length);
                canvas.drawRect(x, y - myFontTop, x + width, y + myFontBottom, myPaint);
            }
        }
    }
    
    protected void redrawText(Canvas canvas, int line, int length, float x, float y) {
        if (length == 0) return;
        
        int fontColor=0xff212121;
        Color color=myTheme.getColor(Theme.Colors.FontColor);
        if (color!=null){
            fontColor = color.value;
        }
        int lastStyle = getModel().getStyle(line, 0);
        TextStyle textStyle = myTheme.getTextStyle(lastStyle);
        if (textStyle != null) applyFontStyle(textStyle.fontStyle);
        else applyFontStyle(Typeface.NORMAL);
        
        int column = 0;
        for (int i = 0; i < length; i++) {
            int style = getModel().getStyle(line, i);
            if (style != lastStyle) {
                if (textStyle != null) {
                    if (textStyle.backgroundColor!=null &&textStyle.backgroundColor.value!=0) {
                        myPaint.setColor(textStyle.backgroundColor.value);
                        float advance = nativeMeasureLine(line, column, i - column);
                        canvas.drawRect(x, y - myFontTop, x + advance, y + myFontBottom, myPaint);
                    }
                    if (textStyle.fontColor!=null &&textStyle.fontColor.value!=0)
                      myPaint.setColor(textStyle.fontColor.value);
                    else
                      myPaint.setColor(fontColor);
                } else {
                    myPaint.setColor(fontColor);
                }
                
                x = drawText(canvas, line, column, i - column, x, y);
                column = i;
                lastStyle = style;
                textStyle = myTheme.getTextStyle(lastStyle);
                if (textStyle != null) applyFontStyle(textStyle.fontStyle);
                else applyFontStyle(Typeface.NORMAL);
            }
        }
        
        if (column < length) {
            if (textStyle != null) {
                applyFontStyle(textStyle.fontStyle);
                if (textStyle.fontColor!=null &&textStyle.fontColor.value!=0)
                    myPaint.setColor(textStyle.fontColor.value);
                else
                    myPaint.setColor(fontColor);
            } else {
                applyFontStyle(Typeface.NORMAL);
                myPaint.setColor(fontColor);
            }
            drawText(canvas, line, column, length - column, x, y);
        }
        
        applyFontStyle(Typeface.NORMAL);
        myPaint.setColor(fontColor);
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
