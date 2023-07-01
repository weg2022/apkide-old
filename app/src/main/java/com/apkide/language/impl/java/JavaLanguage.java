package com.apkide.language.impl.java;

import androidx.annotation.NonNull;

import com.apkide.language.runtime.CodeCompleter;
import com.apkide.language.runtime.Formatter;
import com.apkide.language.runtime.Highlighter;
import com.apkide.language.runtime.Language;

public class JavaLanguage implements Language {
    private final JavaHighlighter highlighter=new JavaHighlighter();
    public JavaLanguage(){

    }
    @NonNull
    @Override
    public String getName() {
        return "Java";
    }

    @NonNull
    @Override
    public String[] getDefaultFilePatterns() {
        return new String[]{"*.java","*.class"};
    }

    @Override
    public Highlighter getHighlighter() {
        return highlighter;
    }

    @Override
    public Formatter getFormatter() {
        return null;
    }

    @Override
    public CodeCompleter getCodeCompleter() {
        return null;
    }
}
