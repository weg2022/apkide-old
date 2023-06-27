package com.apkide.ui.views.editor;

import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.ITALIC;

import android.util.SparseArray;

import androidx.annotation.NonNull;

public class Theme {
    public static final int Plain = 0;
    public static final int Keyword = 1;
    public static final int Operator = 2;
    public static final int Separator = 3;
    public static final int NumberLiteral = 4;
    public static final int StringLiteral = 5;
    public static final int Metadata = 6;
    public static final int Preprocessor = 7;
    public static final int Identifier = 8;
    public static final int NamespaceIdentifier = 9;
    public static final int TypeIdentifier = 10;
    public static final int FunctionIdentifier = 11;
    public static final int Comment = 12;
    public static final int DocComment = 13;

    private int backgroundColor;
    private int fontColor;
    private int lineNumberColor;
    private int caretColor;
    private int caretLineColor;
    private int caretLineNumberColor;
    private int selectionColor;
    private int dragHandleColor;
    private int findMatchColor;
    private int composingColor;
    private int hyperlinkColor;
    private int todoColor;
    private int errorColor;
    private int warningColor;

    private final SparseArray<TextStyle> styles = new SparseArray<>(50);
    private final ColorScheme defaultScheme;
    private ColorScheme scheme;

    public Theme() {
        this.defaultScheme = new ColorScheme() {
            @NonNull
            @Override
            public String getName() {
                return "Default";
            }

            @Override
            public long getVersion() {
                return 0;
            }

            @Override
            public void applyDefault(Theme theme) {
                theme.backgroundColor = 0xffffffff;
                theme.fontColor = 0xff000000;
                theme.caretColor = 0xff000000;
                theme.caretLineColor = 0xfff5f5f5;
                theme.caretLineNumberColor = 0xfffA7db1;
                theme.lineNumberColor = 0xffd0d0d0;
                theme.selectionColor = 0xff0099cc;
                theme.dragHandleColor = 0xfffA7db1;
                theme.findMatchColor = 0xff0099cc;
                theme.composingColor = 0xff7ec482;
                theme.hyperlinkColor = 0xff000000;
                theme.todoColor = 0xff73ad2b;
                theme.errorColor = 0xfff50000;
                theme.warningColor = 0xfff49810;
                theme.setStyle(Plain,new TextStyle(0xff000000));
                theme.setStyle(Keyword, new TextStyle(0xff2c82c8, BOLD));
                theme.setStyle(Operator,new TextStyle(0xff007c1f));
                theme.setStyle(Separator,new TextStyle(0xff0096ff));
                theme.setStyle(NumberLiteral,new TextStyle(0xffbc0000));
                theme.setStyle(StringLiteral,new TextStyle(0xffbc0000));
                theme.setStyle(Metadata,new TextStyle(0xff9E880D));
                theme.setStyle(Preprocessor,new TextStyle(0xff9E880D));
                theme.setStyle(Identifier,new TextStyle(0xff000000));
                theme.setStyle(NamespaceIdentifier,new TextStyle(0xff5d5d5d));
                theme.setStyle(TypeIdentifier,new TextStyle(0xff0096ff));
                theme.setStyle(FunctionIdentifier,new TextStyle(0xff174ad4));
                theme.setStyle(Comment,new TextStyle(0xff009b00));
                theme.setStyle(DocComment,new TextStyle(0xff009b00,ITALIC));
            }
        };
        defaultScheme.applyDefault(this);
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getFontColor() {
        return fontColor;
    }

    public void setFontColor(int fontColor) {
        this.fontColor = fontColor;
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

    public int getDragHandleColor() {
        return dragHandleColor;
    }

    public void setDragHandleColor(int dragHandleColor) {
        this.dragHandleColor = dragHandleColor;
    }

    public int getFindMatchColor() {
        return findMatchColor;
    }

    public void setFindMatchColor(int findMatchColor) {
        this.findMatchColor = findMatchColor;
    }

    public int getComposingColor() {
        return composingColor;
    }

    public void setComposingColor(int composingColor) {
        this.composingColor = composingColor;
    }

    public int getHyperlinkColor() {
        return hyperlinkColor;
    }

    public void setHyperlinkColor(int hyperlinkColor) {
        this.hyperlinkColor = hyperlinkColor;
    }

    public int getTodoColor() {
        return todoColor;
    }

    public void setTodoColor(int todoColor) {
        this.todoColor = todoColor;
    }

    public int getErrorColor() {
        return errorColor;
    }

    public void setErrorColor(int errorColor) {
        this.errorColor = errorColor;
    }

    public int getWarningColor() {
        return warningColor;
    }

    public void setWarningColor(int warningColor) {
        this.warningColor = warningColor;
    }

    public void setStyle(int id, TextStyle style) {
        styles.put(id, style);
    }

    public TextStyle getStyle(int id) {
        return styles.get(id);
    }

    public int getStyleCount() {
        return styles.size();
    }

    public void applyScheme(ColorScheme scheme) {
        if (this.scheme != null) {
            if (this.scheme.getName().equals(scheme.getName())) {
                if (this.scheme.getVersion() > scheme.getVersion()) {
                    return;
                }
            }
        }
        this.scheme = scheme;
        this.scheme.applyDefault(this);
    }


    public void resetDefaults() {
        styles.clear();
        defaultScheme.applyDefault(this);
        if (scheme != null)
            scheme.applyDefault(this);
    }

    public String getName() {
        if (scheme != null)
            return scheme.getName();
        return defaultScheme.getName();
    }

    public long getVersion() {
        if (scheme != null)
            return scheme.getVersion();
        return defaultScheme.getVersion();
    }


    public interface ColorScheme {
        @NonNull
        String getName();

        long getVersion();

        void applyDefault(Theme theme);
    }
}
