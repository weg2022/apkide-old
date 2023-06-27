package com.apkide.ui.views.editor;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE;
import static android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE;
import static android.view.GestureDetector.SimpleOnGestureListener;
import static android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.UNSPECIFIED;
import static android.view.View.MeasureSpec.getMode;
import static android.view.View.MeasureSpec.getSize;
import static com.apkide.ui.views.editor.Theme.ColorScheme;
import static java.lang.Math.max;
import static java.lang.Math.min;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.OverScroller;

import androidx.annotation.NonNull;

import com.apkide.common.AppLog;
import com.apkide.common.text.TextChangeEvent;
import com.apkide.common.text.TextChangeListener;
import com.apkide.common.undo.ExecutionException;

public class EditorView extends View {
    public EditorView(Context context) {
        this(context, null);
    }

    public EditorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private Model model;
    private TextChangeListener textListener;

    private InputConnection inputConnection;

    private Spans spans = Spans.EMPTY;
    private final Theme theme = new Theme();
    private int fontSize = 16;
    private int tabSize = 4;
    private Paint paint;
    private Paint tempPaint;
    private Typeface typeface = Typeface.MONOSPACE;
    private final Paint.FontMetricsInt fontMetrics = new Paint.FontMetricsInt();
    private int fontHeight;
    private int fontTop;
    private int fontBottom;
    private float tabAdvance;
    private float spaceAdvance;

    private final Rect bounds = new Rect(0, 0, 0, 0);
    private OverScroller overScroller;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;

    private void initView() {
        model = new Model();
        model.addTextChangeListener(new TextChangeListener() {
            @Override
            public void textSet() {

            }

            @Override
            public void textChanging(TextChangeEvent event) {

            }

            @Override
            public void textChanged(TextChangeEvent event) {

            }
        });
        model.addTextChangeListener(textListener);

        inputConnection = new BaseInputConnection(this, true) {
            @Override
            public boolean commitText(CharSequence text, int newCursorPosition) {
                return super.commitText(text, newCursorPosition);
            }
        };

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tempPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        overScroller = new OverScroller(getContext());
        gestureDetector = new GestureDetector(getContext(), new SimpleOnGestureListener() {
            @Override
            public void onLongPress(@NonNull MotionEvent e) {

            }
        });

        scaleGestureDetector = new ScaleGestureDetector(getContext(), new SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(@NonNull ScaleGestureDetector detector) {
                return super.onScale(detector);
            }
        });


    }

    public Theme getTheme(){
        return theme;
    }

    public void applyColorScheme(ColorScheme scheme) {
        theme.applyScheme(scheme);
    }

    public void setTextStyle(int id, TextStyle style) {
        theme.setStyle(id, style);
    }

    public TextStyle getTextStyle(int id) {
        return theme.getStyle(id);
    }

    public void setBackgroundColor(int color) {
        theme.setBackgroundColor(color);
    }

    public int getBackgroundColor() {
        return theme.getBackgroundColor();
    }

    public void setFontColor(int color) {
        theme.setFontColor(color);
    }

    public int getFontColor() {
        return theme.getFontColor();
    }

    public void setLineNumberColor(int color) {
        theme.setLineNumberColor(color);
    }

    public int getLineNumberColor() {
        return theme.getLineNumberColor();
    }

    public void setCaretColor(int color) {
        theme.setCaretColor(color);
    }

    public int getCaretColor() {
        return theme.getCaretColor();
    }

    public void setCaretLineColor(int color) {
        theme.setCaretLineColor(color);
    }

    public int getCaretLineColor() {
        return theme.getCaretLineColor();
    }

    public void setCaretLineNumberColor(int color) {
        theme.setCaretLineNumberColor(color);
    }

    public int getCaretLineNumberColor() {
        return theme.getCaretLineNumberColor();
    }

    public void setSelectionColor(int color) {
        theme.setSelectionColor(color);
    }

    public int getSelectionColor() {
        return theme.getSelectionColor();
    }

    public void setDragHandleColor(int color) {
        theme.setDragHandleColor(color);
    }

    public int getDragHandleColor() {
        return theme.getDragHandleColor();
    }

    public void setFindMatchColor(int color) {
        theme.setFindMatchColor(color);
    }

    public int getFindMatchColor() {
        return theme.getFindMatchColor();
    }

    public void setComposingColor(int color) {
        theme.setComposingColor(color);
    }

    public int getComposingColor() {
        return theme.getComposingColor();
    }

    public void setHyperlinkColor(int color) {
        theme.setHyperlinkColor(color);
    }

    public int getHyperlinkColor() {
        return theme.getHyperlinkColor();
    }

    public void setTodoColor(int color) {
        theme.setTodoColor(color);
    }

    public int getTodoColor() {
        return theme.getTodoColor();
    }

    public void setErrorColor(int color) {
        theme.setErrorColor(color);
    }

    public int getErrorColor() {
        return theme.getErrorColor();
    }

    public void setWarningColor(int color) {
        theme.setWarningColor(color);
    }

    public int getWarningColor() {
        return theme.getWarningColor();
    }


    public void setSpans(Spans spans) {
        if (spans != null) {
            this.spans = spans;
            redraw();
        }
    }

    public Spans getSpans() {
        return spans;
    }

    public void setEditable(boolean editable) {
        setReadOnly(!editable);
    }

    public boolean isEditable() {
        return !isReadOnly();
    }

    public void setReadOnly(boolean readOnly) {
        if (model.isReadOnly() != readOnly) {
            model.setReadOnly(readOnly);
            redraw();
        }
    }

    public boolean isReadOnly() {
        return model.isReadOnly();
    }

    public void setText(String text) {
        model.setText(text);
    }

    public void insert(int position, String text) {
        model.insert(position, text);
    }

    public void delete(int start, int length) {
        model.delete(start, length);
    }

    public void replace(int start, int length, String text) {
        model.replace(start, length, text);
    }

    public char getChar(int position) {
        return model.getChar(position);
    }

    public String getTextRange(int start, int length) {
        return model.getTextRange(start, length);
    }

    public String getText() {
        return model.getText();
    }

    public void getChars(int start, int length, char[] dest, int off) {
        model.getChars(start, length, dest, off);
    }

    public int getCharCount() {
        return model.getCharCount();
    }

    public int getLineCount() {
        return model.getLineCount();
    }

    public int getLineAtPosition(int position) {
        return model.getLineIndex(position);
    }

    public int getLineStart(int line) {
        return model.getLineStart(line);
    }

    public String getLineText(int line) {
        return model.getLineText(line);
    }

    public int getLineLength(int line) {
        return model.getLineLength(line);
    }

    public void setUndoLimit(int limit) {
        model.setUndoHistoryLimit(limit);
    }

    public int getUndoLimit() {
        return model.getUndoHistoryLimit();
    }

    public boolean canUndo() {
        return model.canUndo();
    }

    public boolean canRedo() {
        return model.canRedo();
    }

    public void undo() {
        try {
            model.undo();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void redo() {
        try {
            model.redo();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public boolean isCompoundEdit() {
        return model.isCompoundEdit();
    }

    public void beginCompoundEdit() {
        model.beginCompoundEdit();
    }

    public void endCompoundEdit() {
        model.endCompoundEdit();
    }

    public void addTextChangeListener(TextChangeListener listener) {
        model.addTextChangeListener(listener);
    }

    public void removeTextChangeListener(TextChangeListener listener) {
        model.removeTextChangeListener(listener);
    }


    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setTabSize(int tabSize) {
        this.tabSize = tabSize;
    }

    public int getTabSize() {
        return tabSize;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public float getDeviceFontSize() {
        return fontSize * getDensity();
    }

    public float getVisualTabAdvance() {
        return tabSize * spaceAdvance;
    }

    public float getTabAdvance() {
        return tabAdvance;
    }

    public float getSpaceAdvance() {
        return spaceAdvance;
    }

    public int getFontTop() {
        return fontTop;
    }

    public int getFontBottom() {
        return fontBottom;
    }

    public int getFontHeight() {
        return fontHeight;
    }

    private void updatePaintMetrics() {
        paint.setTypeface(typeface);
        paint.setTextSize(getDeviceFontSize());
        paint.getFontMetricsInt(fontMetrics);
        fontTop = fontMetrics.top;
        fontBottom = fontMetrics.bottom;
        fontHeight = fontBottom - fontTop;
        tabAdvance = paint.measureText("\t");
        spaceAdvance = paint.measureText(" ");
    }

    private float getDensity() {
        return getContext().getResources().getDisplayMetrics().density;
    }


    public void updateConfigure() {

        updatePaintMetrics();
    }


    @Override
    public void computeScroll() {
        if (overScroller.computeScrollOffset()) {
            scrollTo(overScroller.getCurrX(), overScroller.getCurrY());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode = getMode(widthMeasureSpec);
        int wSize = getSize(widthMeasureSpec);
        int hMode = getMode(heightMeasureSpec);
        int hSize = getSize(heightMeasureSpec);

        int width, height;
        switch (wMode) {
            case UNSPECIFIED:
                width = max(getDefaultMeasureWidth(), wSize);
                break;
            case AT_MOST:
                width = min(getDefaultMeasureWidth(), wSize);
                break;
            case EXACTLY:
            default:
                width = wSize;
                break;
        }

        switch (hMode) {
            case UNSPECIFIED:
                height = max(getDefaultMeasureHeight(), hSize);
                break;
            case AT_MOST:
                height = min(getDefaultMeasureHeight(), hSize);
                break;
            case EXACTLY:
            default:
                height = hSize;
                break;
        }
        setMeasuredDimension(width, height);
    }

    protected int getDefaultMeasureWidth() {
        return 2000;
    }

    protected int getDefaultMeasureHeight() {
        return 2000;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = gestureDetector.onTouchEvent(event);
        if (handled) {

        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return gestureDetector.onGenericMotionEvent(event);
        }
        return super.onGenericMotionEvent(event);
    }


    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.initialCapsMode = inputConnection.getCursorCapsMode(0);
        outAttrs.inputType = TYPE_CLASS_TEXT | TYPE_TEXT_FLAG_MULTI_LINE |
                TYPE_TEXT_FLAG_IME_MULTI_LINE;

        return inputConnection;
    }


    public void redraw() {
        redraw(true);
    }

    public void redraw(boolean ui) {
        redraw(ui, true);
    }

    public void redraw(boolean ui, boolean updateRect) {
        if (ui) {
            if (updateRect)
                updateBounds();
            invalidate();
        } else {
            post(() -> {
                if (updateRect)
                    updateBounds();
                invalidate();
            });
        }
    }

    private void updateBounds() {
        if (!getLocalVisibleRect(bounds)) {
            getGlobalVisibleRect(bounds);
            AppLog.s("getGlobalVisibleRect :" + bounds.toShortString());
        } else {
            AppLog.s("getLocalVisibleRect :" + bounds.toShortString());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
