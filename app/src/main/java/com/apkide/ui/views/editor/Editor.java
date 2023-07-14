package com.apkide.ui.views.editor;

import static com.apkide.engine.SyntaxKind.ClassIdentifier;
import static com.apkide.engine.SyntaxKind.Comment;
import static com.apkide.engine.SyntaxKind.DocumentationComment;
import static com.apkide.engine.SyntaxKind.FunctionIdentifier;
import static com.apkide.engine.SyntaxKind.Identifier;
import static com.apkide.engine.SyntaxKind.Keyword;
import static com.apkide.engine.SyntaxKind.Metadata;
import static com.apkide.engine.SyntaxKind.NamespaceIdentifier;
import static com.apkide.engine.SyntaxKind.NumberLiteral;
import static com.apkide.engine.SyntaxKind.Operator;
import static com.apkide.engine.SyntaxKind.ParameterIdentifier;
import static com.apkide.engine.SyntaxKind.Separator;
import static com.apkide.engine.SyntaxKind.StringLiteral;
import static com.apkide.engine.SyntaxKind.TypeIdentifier;
import static com.apkide.engine.SyntaxKind.VariableIdentifier;
import static com.apkide.engine.SyntaxKind.values;
import static java.lang.Math.max;
import static java.lang.Math.min;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.apkide.engine.SyntaxKind;

public class Editor extends View implements ModelListener {
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

    private final Rect myRect = new Rect(0, 0, 0, 0);

    private EditorColor myFontColor;
    private EditorColor myCaretColor;
    private EditorColor myCaretLineColor;
    private EditorColor myLineNumberColor;
    private EditorColor myWhitespaceColor;
    private EditorColor mySelectionColor;
    private EditorColor mySelectionBackgroundColor;
    private final TextAttribute[] myAttributes = new TextAttribute[values().length + 1];
    private final TextPaint myPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private Typeface myTypeface = Typeface.MONOSPACE;
    private int myFontSize = 18;
    private String myFontFeature;
    private final Paint.FontMetrics myFontMetrics = new Paint.FontMetrics();
    private float myFontTop;
    private float myFontBottom;
    private float myFontHeight;
    private float mySpaceCharWidth;
    private int myTabSize = 4;
    private boolean myUseSpaces;
    private int myIndentationSize = 4;
    private int myMaxColumn;
    private int myMaxColumnWidthLine;
    private float myMaxWidth;
    private float myMaxHeight;
    private float mySidebarWidth;
    private float myRightSpaceWidth;
    private float myBottomSpaceWidth;
    private Model myModel;

    private void initView() {
        registerColorScheme();
        syncConfigure();
        setModel(new Model());
    }


    public void syncConfigure() {
        if (myTypeface != null)
            myPaint.setTypeface(myTypeface);
        myPaint.setTextSize(getDeviceFontSize());
        if (myFontFeature != null)
            myPaint.setFontFeatureSettings(myFontFeature);
        myPaint.getFontMetrics(myFontMetrics);
        myFontTop = myFontMetrics.top;
        myFontBottom = myFontMetrics.bottom;
        myFontHeight = myFontBottom - myFontTop;
        mySpaceCharWidth = myPaint.measureText("M");
    }

    public void setTypeface(Typeface typeface) {
        myTypeface = typeface;
    }

    public Typeface getTypeface() {
        return myTypeface;
    }

    public void setFontFeature(String fontFeature) {
        myFontFeature = fontFeature;
    }

    public String getFontFeature() {
        return myFontFeature;
    }

    public void setFontSize(int fontSize) {
        myFontSize = fontSize;
    }

    public int getFontSize() {
        return myFontSize;
    }

    public float getDeviceFontSize() {
        return myFontSize * getDensity();
    }

    public float getDensity() {
        return getContext().getResources().getDisplayMetrics().density;
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
        return mySpaceCharWidth * myTabSize;
    }

    public void setTabSize(int tabSize) {
        myTabSize = tabSize;
    }

    public int getTabSize() {
        return myTabSize;
    }

    public void setUseSpaces(boolean useSpaces) {
        myUseSpaces = useSpaces;
    }

    public boolean isUseSpaces() {
        return myUseSpaces;
    }

    public void setIndentationSize(int indentationSize) {
        myIndentationSize = indentationSize;
    }

    public int getIndentationSize() {
        return myIndentationSize;
    }


    protected void registerColorScheme() {

    }

    public void setFontColor(@NonNull EditorColor fontColor) {
        myFontColor = fontColor;
    }

    @NonNull
    public EditorColor getFontColor() {
        return myFontColor;
    }

    public void setCaretColor(@NonNull EditorColor caretColor) {
        myCaretColor = caretColor;
    }

    @NonNull
    public EditorColor getCaretColor() {
        return myCaretColor;
    }

    public void setCaretLineColor(@NonNull EditorColor caretLineColor) {
        myCaretLineColor = caretLineColor;
    }

    @NonNull
    public EditorColor getCaretLineColor() {
        return myCaretLineColor;
    }

    public void setLineNumberColor(@NonNull EditorColor lineNumberColor) {
        myLineNumberColor = lineNumberColor;
    }

    @NonNull
    public EditorColor getLineNumberColor() {
        return myLineNumberColor;
    }

    public void setWhitespaceColor(@NonNull EditorColor whitespaceColor) {
        myWhitespaceColor = whitespaceColor;
    }

    @NonNull
    public EditorColor getWhitespaceColor() {
        return myWhitespaceColor;
    }

    public void setSelectionColor(@NonNull EditorColor selectionColor) {
        mySelectionColor = selectionColor;
    }

    @NonNull
    public EditorColor getSelectionColor() {
        return mySelectionColor;
    }

    public void setSelectionBackgroundColor(@NonNull EditorColor selectionBackgroundColor) {
        mySelectionBackgroundColor = selectionBackgroundColor;
    }

    @NonNull
    public EditorColor getSelectionBackgroundColor() {
        return mySelectionBackgroundColor;
    }

    public void setKeywordTextAttribute(@NonNull TextAttribute attribute) {
        registerTextAttribute(Keyword, attribute);
    }

    @NonNull
    public TextAttribute getKeywordTextAttribute() {
        return getTextAttribute(Keyword);
    }

    public void setOperatorTextAttribute(@NonNull TextAttribute attribute) {
        registerTextAttribute(Operator, attribute);
    }

    @NonNull
    public TextAttribute getOperatorTextAttribute() {
        return getTextAttribute(Operator);
    }

    public void setSeparatorTextAttribute(@NonNull TextAttribute attribute) {
        registerTextAttribute(Separator, attribute);
    }

    @NonNull
    public TextAttribute getSeparatorTextAttribute() {
        return getTextAttribute(Separator);
    }

    public void setStringLiteralTextAttribute(@NonNull TextAttribute attribute) {
        registerTextAttribute(StringLiteral, attribute);
    }

    @NonNull
    public TextAttribute getStringLiteralTextAttribute() {
        return getTextAttribute(StringLiteral);
    }

    public void setNumberLiteralTextAttribute(@NonNull TextAttribute attribute) {
        registerTextAttribute(NumberLiteral, attribute);
    }

    @NonNull
    public TextAttribute getNumberLiteralTextAttribute() {
        return getTextAttribute(NumberLiteral);
    }


    public void setMetadataTextAttribute(@NonNull TextAttribute attribute) {
        registerTextAttribute(Metadata, attribute);
    }

    @NonNull
    public TextAttribute getMetadataTextAttribute() {
        return getTextAttribute(Metadata);
    }

    public void setIdentifierTextAttribute(@NonNull TextAttribute attribute) {
        registerTextAttribute(Identifier, attribute);
    }

    @NonNull
    public TextAttribute getIdentifierTextAttribute() {
        return getTextAttribute(Identifier);
    }

    public void setNamespaceIdentifierTextAttribute(@NonNull TextAttribute attribute) {
        registerTextAttribute(NamespaceIdentifier, attribute);
    }

    @NonNull
    public TextAttribute getNamespaceIdentifierTextAttribute() {
        return getTextAttribute(NamespaceIdentifier);
    }


    public void setClassIdentifierTextAttribute(@NonNull TextAttribute attribute) {
        registerTextAttribute(ClassIdentifier, attribute);
    }

    @NonNull
    public TextAttribute getClassIdentifierTextAttribute() {
        return getTextAttribute(ClassIdentifier);
    }


    public void setTypeIdentifierTextAttribute(@NonNull TextAttribute attribute) {
        registerTextAttribute(TypeIdentifier, attribute);
    }

    @NonNull
    public TextAttribute getTypeIdentifierTextAttribute() {
        return getTextAttribute(TypeIdentifier);
    }

    public void setVariableIdentifierTextAttribute(@NonNull TextAttribute attribute) {
        registerTextAttribute(VariableIdentifier, attribute);
    }

    @NonNull
    public TextAttribute getVariableIdentifierTextAttribute() {
        return getTextAttribute(VariableIdentifier);
    }


    public void setFunctionIdentifierTextAttribute(@NonNull TextAttribute attribute) {
        registerTextAttribute(FunctionIdentifier, attribute);
    }

    @NonNull
    public TextAttribute getFunctionIdentifierTextAttribute() {
        return getTextAttribute(FunctionIdentifier);
    }

    public void setParameterIdentifierTextAttribute(@NonNull TextAttribute attribute) {
        registerTextAttribute(ParameterIdentifier, attribute);
    }

    @NonNull
    public TextAttribute getParameterIdentifierTextAttribute() {
        return getTextAttribute(ParameterIdentifier);
    }

    public void setCommentTextAttribute(@NonNull TextAttribute attribute) {
        registerTextAttribute(Comment, attribute);
    }

    @NonNull
    public TextAttribute getCommentTextAttribute() {
        return getTextAttribute(Comment);
    }

    public void setDocCommentTextAttribute(@NonNull TextAttribute attribute) {
        registerTextAttribute(DocumentationComment, attribute);
    }

    @NonNull
    public TextAttribute getDocCommentTextAttribute() {
        return getTextAttribute(DocumentationComment);
    }

    protected void registerTextAttribute(@NonNull SyntaxKind kind, @NonNull TextAttribute attribute) {
        myAttributes[kind.intValue()] = attribute;
    }

    @NonNull
    protected TextAttribute getTextAttribute(@NonNull SyntaxKind kind) {
        return myAttributes[kind.intValue()];
    }

    public void setModel(Model model) {
        if (myModel != null)
            myModel.removeModelListener(this);

        myModel = model;
        myModel.addModelListener(this);
        invalidate();
    }

    @NonNull
    public Model getModel() {
        return myModel;
    }

    public char getChar(int position) {
        return myModel.charAt(position);
    }

    public int getLineCount() {
        return myModel.getLineCount();
    }

    public int getLineStart(int line) {
        return myModel.getLineStart(line);
    }

    public int getLineLength(int line) {
        return myModel.getLineLength(line);
    }

    public LineInformation getLineInformation(int line) {
        return myModel.getLineInformation(line);
    }


    public float getMaxWidth() {
        return myMaxWidth;
    }

    public float getMaxHeight() {
        return myMaxHeight;
    }

    public float getRightSpaceWidth() {
        return myRightSpaceWidth;
    }

    public float getBottomSpaceWidth() {
        return myBottomSpaceWidth;
    }

    public float getSidebarWidth() {
        return mySidebarWidth;
    }

    public int getMaxColumn() {
        return myMaxColumn;
    }

    public int getMaxColumnWidthLine() {
        return myMaxColumnWidthLine;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                (int) max(myMaxWidth, getSuggestedMinimumWidth()),
                (int) max(myMaxHeight, getSuggestedMinimumHeight()));
    }

    private void computeLayout() {
        myMaxColumn = 0;
        myMaxColumnWidthLine = 0;
        mySidebarWidth = 0;
        myMaxWidth = 0;
        myMaxHeight = 0;
        int lineCount = getLineCount();
        for (int i = 0; i < lineCount; i++) {
            int maxCol = computeLineColumnCount(i);
            if (maxCol > myMaxColumn) {
                myMaxColumn = maxCol;
                myMaxColumnWidthLine = i;
            }
        }

        int spaceColumn = ((myMaxColumn * 5) / 4) - myMaxColumn;
        spaceColumn = min(80, spaceColumn);
        myRightSpaceWidth = spaceColumn * mySpaceCharWidth;

        int lines = (lineCount * 5 / 4);
        lines = min(5, lines);
        myBottomSpaceWidth = lines * myFontHeight;

        mySidebarWidth += myPaint.measureText(Integer.toBinaryString(getLineCount()));

        myMaxWidth = myMaxColumn * mySpaceCharWidth;
        myMaxHeight = getLineCount() * myFontHeight;

        myMaxWidth += myRightSpaceWidth;
        myMaxHeight += myBottomSpaceWidth;
        requestLayout();
    }

    private int computeLineColumnCount(int line) {
        LineInformation information = getLineInformation(line);
        int col = 0;
        int length = information.length;
        for (int i = 0; i < length; i++) {
            char c = getChar(information.offset + i);
            if (c == '\t') {
                col += myTabSize;
            } else {
                col++;
            }
        }
        return col;
    }

    public void redraw() {
        getLocalVisibleRect(myRect);
        invalidate();
    }

    @Override
    public void textSet(@NonNull Model model) {

        computeLayout();
        invalidate();
    }

    @Override
    public void textPreInsert(@NonNull Model model, int startLine, int startColumn, int endLine, int endColumn, @NonNull CharSequence text) {

    }

    @Override
    public void textInserted(@NonNull Model model, int startLine, int startColumn, int endLine, int endColumn, @NonNull CharSequence text) {
        computeLayout();
        invalidate();
    }

    @Override
    public void textPreRemove(@NonNull Model model, int startLine, int startColumn, int endLine, int endColumn) {

    }

    @Override
    public void textRemoved(@NonNull Model model, int startLine, int startColumn, int endLine, int endColumn) {

        computeLayout();
        invalidate();
    }


    protected void applyFontStyle(@NonNull TextAttribute attribute, @NonNull Paint paint) {
        if (attribute.isBoldItalic()) {
            paint.setTextSkewX(-0.25f);
            paint.setFakeBoldText(true);
        } else if (attribute.isItalic()) {
            paint.setTextSkewX(-0.25f);
        } else if (attribute.isBold()) {
            paint.setFakeBoldText(true);
        } else {
            paint.setFakeBoldText(false);
            paint.setTextSkewX(0);
        }
    }

    protected void restoreDefaultFontStyle(@NonNull Paint paint) {
        paint.setFakeBoldText(false);
        paint.setTextSkewX(0);
    }


    public float getLineBaseline(int line) {
        return (line + 1) * getFontHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (myRect.isEmpty()) {
            Log.d(TAG, "onDraw: rect is empty. " + myRect.toShortString());
            return;
        }
        canvas.save();
        int startLine = (int) (myRect.top / getFontHeight());
        int endLine = (int) (myRect.bottom / getFontHeight());
        endLine += 1;
        startLine = Math.max(0, startLine);
        endLine = Math.min(endLine, getLineCount());
        float x = getSidebarWidth();
        float y = getLineBaseline(startLine);
        for (int i = startLine; i < endLine; i++) {
            drawLine(canvas, i, x, y);
            y += getFontHeight();
        }
        canvas.restore();
    }

    protected void drawLine(Canvas canvas, int line, float x, float y) {

    }

    protected float measureText(int start, int end) {
        float rect = myModel.nativeMeasureText(start, end, myPaint);
        float oTab = myPaint.measureText("\t");
        float tab = getTabCharWidth();
        for (int i = start; i < end; i++) {
            char c = getChar(i);
            if (c == '\t') {
                rect -= oTab;
                rect += tab;
            }
        }
        return rect;
    }
}
