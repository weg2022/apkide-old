package com.apkide.ui.views.editor;

import android.graphics.Typeface;

public class TextAttribute {
    private EditorColor backgroundColor;
    private EditorColor fontColor;
    private int fontStyle;

    public TextAttribute(EditorColor fontColor) {
        this(fontColor, Typeface.NORMAL);
    }

    public TextAttribute(EditorColor fontColor, int fontStyle) {
        this(null, fontColor, fontStyle);
    }

    public TextAttribute(EditorColor backgroundColor, EditorColor fontColor, int fontStyle) {
        this.backgroundColor = backgroundColor;
        this.fontColor = fontColor;
        this.fontStyle = fontStyle;
    }

    public EditorColor getBackgroundColor() {
        return backgroundColor;
    }

    public boolean hasBackground(){
        return backgroundColor!=null && backgroundColor.alpha()!=0;
    }

    public EditorColor getFontColor() {
        return fontColor;
    }

    public int getFontStyle() {
        return fontStyle;
    }

    public boolean isBold() {
        return (fontStyle & Typeface.BOLD) != 0;
    }

    public boolean isItalic() {
        return (fontStyle & Typeface.ITALIC) != 0;
    }

    public boolean isBoldItalic() {
        return (fontStyle & Typeface.BOLD_ITALIC) != 0;
    }

    public boolean isNormal() {
        return fontStyle <= 0 || fontStyle > Typeface.BOLD_ITALIC;
    }
}
