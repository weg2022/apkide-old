package com.apkide.views.editor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.apkide.common.ColorScheme;
import com.apkide.common.Document;
import com.apkide.common.DocumentEvent;
import com.apkide.common.DocumentListener;
import com.apkide.common.IntelliJLightColorScheme;
import com.apkide.common.LineBreak;
import com.apkide.common.TextAttribute;
import com.apkide.common.TextChangeEvent;
import com.apkide.common.TextChangeListener;
import com.apkide.common.TextUtilities;

import java.util.ArrayList;
import java.util.List;

public class Editor extends View implements DocumentListener {
    public Editor(Context context) {
        super(context);
        initView();
    }

    public Editor(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public Editor(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private static final String TAG = "Editor";

    private static final char TAB = '\t';
    private static final char LF = '\n';
    private static final char CR = '\r';
    private static final char SPACE = ' ';
    private static final char IS = '\u3000';
    private static final char SPACE_SIGN = '\u00b7';
    private static final char IDEOGRAPHIC_SPACE_SIGN = '\u00b0';
    private static final char TAB_SIGN = '\u00bb';
    private static final char CARRIAGE_RETURN_SIGN = '\u00a4';
    private static final char LINE_FEED_SIGN = '\u00b6';
    private static final String ENABLED_LIGATURE = "'liga' on";
    private static final String DISABLE_LIGATURE = "'liga' off";


    private final Rect visibleRegion = new Rect();
    private final Typeface typeface = Typeface.MONOSPACE;
    private final TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Paint.FontMetricsInt fontMetrics = new Paint.FontMetricsInt();
    private int fontSize = 18;
    private int lineTop;
    private int lineBottom;
    private int lineHeight;
    private float lineSpacing = 1.2f;
    private float spaceAdvance;//' ' character width
    private float originalTabAdvance;//'\t' character width
    private float tabAdvance;//(tabSize * spaceAdvance) TAB width
    private boolean ligatureEnabled = true;
    private boolean useTabCharacter = true;
    private int tabSize = 4;
    private boolean indentationEnabled = true;
    private int indentationSize = 4;

    private boolean showLineNumber = true;
    private boolean showCaretLine = true;
    private boolean showWhitespace = true;

    private int backgroundColor = 0;
    private int textColor = 0xFF080808;
    private int dividerColor = 0xFFD4D4D4;
    private int lineNumberColor = 0xFFADADAD;
    private int caretColor = 0xFF000000;
    private int caretLineColor = 0xFFFCFAED;
    private int caretLineNumberColor = 0;
    private int selectionColor = 0xFFA6D2FF;
    private int selectionHandleColor = 0xFF000000;
    private int searchResultColor = 0xFFFFE959;
    private ColorScheme colorScheme;

    private int caretOffset = 0;
    private int caretLine = 0;
    private int selectAnchor = 0;
    private int selectEdge = 0;

    private boolean editable;
    private Document document;
    private TextChangeEvent changeEvent;
    private DocumentEvent documentEvent;
    private boolean isForwarding = true;
    private int rememberedLengthOfDocument;

    private final DocumentEvent originalEvent = new DocumentEvent();
    private final List<TextChangeListener> textChangeListeners = new ArrayList<>(1);


    private void initView() {

        setDefaults();
    }

    private void setDefaults() {
        setEditable(getDefaultEditable());
        setDocument(getDefaultDocument());
        setColorScheme(getDefaultColorScheme());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Default Methods Beginning
    ///////////////////////////////////////////////////////////////////////////

    @NonNull
    protected ColorScheme getDefaultColorScheme() {
        return new IntelliJLightColorScheme();
    }

    @NonNull
    protected Document getDefaultDocument() {
        return new Document();
    }

    protected boolean getDefaultEditable() {
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Code Styled
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
    }

    public int getLineNumberColor() {
        return lineNumberColor;
    }

    public void setLineNumberColor(int lineNumberColor) {
        this.lineNumberColor = lineNumberColor;
    }

    public int getCaretColor() {
        return caretColor;
    }

    public void setCaretColor(int caretColor) {
        this.caretColor = caretColor;
    }

    public int getCaretLineColor() {
        return caretLineColor;
    }

    public void setCaretLineColor(int caretLineColor) {
        this.caretLineColor = caretLineColor;
    }

    public int getCaretLineNumberColor() {
        return caretLineNumberColor;
    }

    public void setCaretLineNumberColor(int caretLineNumberColor) {
        this.caretLineNumberColor = caretLineNumberColor;
    }

    public int getSelectionColor() {
        return selectionColor;
    }

    public void setSelectionColor(int selectionColor) {
        this.selectionColor = selectionColor;
    }

    public int getSelectionHandleColor() {
        return selectionHandleColor;
    }

    public void setSelectionHandleColor(int selectionHandleColor) {
        this.selectionHandleColor = selectionHandleColor;
    }

    public int getSearchResultColor() {
        return searchResultColor;
    }

    public void setSearchResultColor(int searchResultColor) {
        this.searchResultColor = searchResultColor;
    }

    public void setColorScheme(@NonNull ColorScheme colorScheme) {
        this.colorScheme = colorScheme;
    }

    @NonNull
    public ColorScheme getColorScheme() {
        return colorScheme;
    }

    public String getColorSchemeName() {
        return colorScheme.getName();
    }

    public boolean isDarkColorScheme() {
        return colorScheme.isDark();
    }

    public void restoreDefaultTextAttributes() {
        colorScheme.restoreDefaults();
    }

    public void registerTextAttribute(int id, @NonNull TextAttribute attribute) {
        colorScheme.register(id, attribute);
    }

    public void unregisterTextAttribute(int id) {
        colorScheme.unregister(id);
    }

    public boolean isTextAttributeRegistered(int id) {
        return colorScheme.isRegister(id);
    }

    @Nullable
    public TextAttribute getTextAttribute(int id) {
        return colorScheme.getAttribute(id);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Text Editing Method
    ///////////////////////////////////////////////////////////////////////////

    public void setDocument(@NonNull Document document) {
        if (this.document != null)
            this.document.removeDocumentListener(this);
        this.document = document;
        this.document.addDocumentListener(this);
    }

    public void addTextChangeListener(@NonNull TextChangeListener listener) {
        if (!textChangeListeners.contains(listener)) {
            textChangeListeners.add(listener);
        }
    }

    public void removeTextChangeListener(@NonNull TextChangeListener listener) {
        textChangeListeners.remove(listener);
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isEditable() {
        return editable;
    }

    @NonNull
    public Document getDocument() {
        return document;
    }

    public void setText(@NonNull String text) {
        document.setText(text);
    }

    public void insertText(int pos, @NonNull String text) {
        if (!isEditable()) return;
        document.insert(pos, text);
    }

    public void deleteText(int pos, int length) {
        if (!isEditable()) return;
        document.delete(pos, length);
    }

    public void replaceText(int pos, int length, @NonNull String text) {
        if (!isEditable()) return;
        document.replace(pos, length, text);
    }

    public char charAt(int pos) {
        return document.charAt(pos);
    }

    @NonNull
    public String getText(int pos, int length) {
        return document.getText(pos, length);
    }

    @NonNull
    public String getText() {
        return document.getText();
    }

    public int getCharCount() {
        return document.getCharCount();
    }

    public int getLineCount() {
        return document.getLineCount();
    }

    public int getLineNumber(int pos) {
        return document.findLineNumber(pos);
    }

    public int getLineStart(int line) {
        return document.getLineStart(line);
    }

    public int getLineLength(int line) {
        return document.getLineLength(line);
    }

    public int getPhysicalLineLength(int line) {
        return document.getPhysicalLineLength(line);
    }

    @NonNull
    public String getPhysicalLine(int line) {
        return document.getPhysicalLine(line);
    }

    @NonNull
    public String getLine(int line) {
        return document.getLine(line);
    }


    public void beginBatchEdit() {
        document.beginBatchEdit();
    }

    public void endBatchEdit() {
        document.endBatchEdit();
    }

    public boolean isBatchEdit() {
        return document.isBatchEdit();
    }

    public boolean undoable() {
        return document.undoable();
    }

    public boolean redoable() {
        return document.redoable();
    }

    public void undo() {
        document.undo();
    }

    public void redo() {
        document.redo();
    }

    public void setEncoding(@NonNull String encoding) {
        document.setEncoding(encoding);
    }

    @NonNull
    public String getEncoding() {
        return document.getEncoding();
    }

    public void setLineBreak(@NonNull LineBreak lineBreak) {
        document.setLineBreak(lineBreak);
    }

    @NonNull
    public LineBreak getLineBreak() {
        return document.getLineBreak();
    }

    public int getCaretLine() {
        return caretLine;
    }

    public int getCaretPosition() {
        return caretOffset;
    }

    public boolean isSelectionText() {
        return selectAnchor != selectEdge;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Internal Measure And Drawing
    ///////////////////////////////////////////////////////////////////////////

    private void internalDrawText(Canvas c, int start, int end, float x, float y, @NonNull Paint p) {
        document.drawText(c, start, end, x, y, p);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void internalDrawTextRun(Canvas c, int start, int end, int contextStart, int contextEnd,
                                     float x, float y, boolean isRtl, @NonNull Paint p) {
        document.drawTextRun(c, start, end, contextStart, contextEnd, x, y, isRtl, p);
    }

    private float internalMeasureText(int start, int end, @NonNull Paint p) {
        return document.measureText(start, end, p);
    }

    private int internalGetTextWidths(int start, int end, @NonNull float[] widths, @NonNull Paint p) {
        return document.getTextWidths(start, end, widths, p);
    }

    protected void drawText(Canvas canvas, int start, int end, float x, float y) {
        //ignore context index and RTL
        int ts = start;
        for (int i = ts; i < end; i++) {
            if (charAt(i) == TAB) {
                drawTextCompat(canvas, ts, i, ts, i, false, x, y);
                x += getTextAdvance(ts, i);
                ts = i;
            }
        }
        if (ts < end)
            drawTextCompat(canvas, ts, end, ts, end, false, x, y);
    }

    protected float drawTextAdvance(Canvas canvas, int start, int end, float x, float y) {
        //ignore context index and RTL
        int ts = start;
        for (int i = ts; i < end; i++) {
            if (charAt(i) == TAB) {
                drawTextCompat(canvas, ts, i, ts, i, false, x, y);
                x += getTextAdvance(ts, i);
                ts = i;
            }
        }
        if (ts < end) {
            drawTextCompat(canvas, ts, end, ts, end, false, x, y);
            x += getTextAdvance(ts, end);
        }
        return x;
    }

    private void drawTextCompat(Canvas canvas, int start, int end, int contextStart, int contextEnd, boolean isRtl, float x, float y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            internalDrawTextRun(canvas, start, end, contextStart, contextEnd, x, y, isRtl, paint);
        else
            internalDrawText(canvas, start, end, x, y, paint);
    }

    public float getTextAdvance(int start, int end) {
        float advance = internalMeasureText(start, end, paint);
        for (int i = 0; i < end; i++) {
            if (document.charAt(i) == '\t') {
                advance -= originalTabAdvance;
                advance += tabAdvance;
            }
        }
        return advance;
    }

    public float getTextAdvances(int start, int end, float[] advances) {
        float width = internalGetTextWidths(start, end, advances, paint);
        for (int i = 0; i < end; i++) {
            if (document.charAt(i) == '\t') {
                width -= originalTabAdvance;
                width += tabAdvance;
                advances[i] = tabAdvance;
            }
        }
        return width;
    }

    public int getLineTop() {
        return lineTop;
    }

    public int getLineBottom() {
        return lineBottom;
    }

    public int getLineHeight() {
        return lineHeight;
    }

    public float getLineBaseline(int line) {
        return (line + 1) * getLineHeight();
    }

    public int getFirstVisibleLine() {
        int line = visibleRegion.top / getLineHeight();
        if (line <= 0) return 0;
        return Math.min(getLineCount(), line);
    }

    public int getLastVisibleLine() {
        int line = visibleRegion.bottom + 1 / getLineHeight();
        if (line <= 0) return 0;
        return Math.min(getLineCount(), line);
    }

    public void redraw() {
        getLocalVisibleRect(visibleRegion);
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        onRendering(canvas, paint);
        canvas.restore();
    }

    protected void onRendering(Canvas canvas, TextPaint paint) {
        if (getBackgroundColor() != 0)
            canvas.drawColor(getBackgroundColor());
        int firstLine = getFirstVisibleLine();
        int lastLine = getLastVisibleLine();
        float leftWidth = 0;
        float x = leftWidth;
        float y = getLineBaseline(firstLine);
        for (int i = firstLine; i < lastLine; i++) {
            onRenderingLine(canvas, paint, i, leftWidth, x, y);
            y += getLineHeight();
        }
    }

    protected void onRenderingLine(Canvas canvas, TextPaint paint, int line, float leftWidth, float x, float y) {
        int lineStart = getLineStart(line);
        if (lineStart < 0) return;

        if (showCaretLine && line == getCaretLine() && caretLineColor != 0) {
            paint.setColor(getCaretLineColor());
            canvas.drawRect(
                    visibleRegion.left,y+getLineTop(),
                    visibleRegion.right,y+getLineBottom(),paint);
        }

        if (showLineNumber && lineNumberColor != 0) {
            if (caretLine == line && caretLineNumberColor != 0)
                paint.setColor(getCaretLineNumberColor());
            else
                paint.setColor(getLineNumberColor());
            paint.setTextAlign(Paint.Align.RIGHT);
            String number = Integer.toString(line + 1);
            canvas.drawText(number, x, y, paint);
            paint.setTextAlign(Paint.Align.LEFT);
        }

    }


    @Override
    public void documentAboutToBeChanged(@NonNull DocumentEvent event) {
        rememberedLengthOfDocument = document.length();
        documentEvent = event;
        rememberEventData(documentEvent);
        fireTextChanging();
        invalidate();
    }

    @Override
    public void documentChanged(@NonNull DocumentEvent event) {
        if (documentEvent == null || event != documentEvent)
            return;

        if (isPatchedEvent(event) || (event.offset == 0 && event.length == rememberedLengthOfDocument))
            fireTextSet();
        else
            fireTextChanged();
        invalidate();
    }

    private boolean isPatchedEvent(DocumentEvent event) {
        return originalEvent.offset != event.offset || originalEvent.length != event.length || !originalEvent.text.equals(event.text);
    }

    private void rememberEventData(DocumentEvent event) {
        originalEvent.offset = event.offset;
        originalEvent.length = event.length;
        originalEvent.text = event.text;
    }

    private void fireTextChanged() {
        if (!isForwarding) return;

        if (changeEvent == null) return;

        if (!textChangeListeners.isEmpty()) {
            for (TextChangeListener textChangeListener : textChangeListeners)
                textChangeListener.textChanged(changeEvent);
        }
    }

    private void fireTextSet() {
        if (!isForwarding) return;

        if (changeEvent == null) return;

        if (!textChangeListeners.isEmpty()) {
            for (TextChangeListener textChangeListener : textChangeListeners)
                textChangeListener.textSet(changeEvent);
        }
    }

    private void fireTextChanging() {
        if (!isForwarding) return;

        Document document = documentEvent.document;
        if (document == null) return;

        changeEvent = new TextChangeEvent();
        changeEvent.start = documentEvent.offset;
        changeEvent.replaceCharCount = documentEvent.length;
        changeEvent.replaceLineCount = TextUtilities.getLineCount(document, documentEvent.offset, documentEvent.length) - 1;
        changeEvent.text = documentEvent.text;
        changeEvent.newCharCount = (documentEvent.text == null ? 0 : documentEvent.text.length());
        changeEvent.newLineCount = (documentEvent.text == null ? 0 : TextUtilities.lineCount(documentEvent.text));

        if (!textChangeListeners.isEmpty()) {
            for (TextChangeListener textChangeListener : textChangeListeners)
                textChangeListener.textChanging(changeEvent);
        }
    }

    public void resumeForwardingDocumentChanges() {
        isForwarding = true;
        fireTextSet();
    }

    public void stopForwardingDocumentChanges() {
        isForwarding = false;
    }


}
