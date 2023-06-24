package com.apktool.cl.api.engine.formatter;

import androidx.annotation.NonNull;

public class CodeFormatterSizeOptions {
    public int indentationSize;
    public int tabSize;
    public int maxLineWidth;

    public CodeFormatterSizeOptions() {
        this.setDefaults();
    }

    @Override
    @NonNull
    public CodeFormatterSizeOptions clone() {
        CodeFormatterSizeOptions options;
        try {
            options = (CodeFormatterSizeOptions) super.clone();
        } catch (CloneNotSupportedException e) {
            options = new CodeFormatterSizeOptions();
        }

        options.indentationSize = indentationSize;
        options.tabSize = tabSize;
        options.maxLineWidth = maxLineWidth;
        return options;
    }

    private void setDefaults() {
        this.indentationSize = 4;
        this.tabSize = 4;
        this.maxLineWidth = 100;
    }
}
