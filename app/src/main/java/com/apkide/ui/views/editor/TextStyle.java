package com.apkide.ui.views.editor;

public class TextStyle {
    public int background;
    public int fontColor;
    public int fontStyle;

    public TextStyle(int fontColor){
        this(fontColor,0);
    }

    public TextStyle(int fontColor, int fontStyle) {
        this(0,fontColor,fontStyle);
    }

    public TextStyle(int background, int fontColor, int fontStyle) {
        this.background = background;
        this.fontColor = fontColor;
        this.fontStyle = fontStyle;
    }
}
