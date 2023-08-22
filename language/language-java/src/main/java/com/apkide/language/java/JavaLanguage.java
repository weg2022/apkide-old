package com.apkide.language.java;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.language.api.Assembly;
import com.apkide.language.api.CodeAnalyzer;
import com.apkide.language.api.CodeCompleter;
import com.apkide.language.api.CodeFormatter;
import com.apkide.language.api.CodeHighlighter;
import com.apkide.language.api.CodeNavigation;
import com.apkide.language.api.CodeRefactor;
import com.apkide.language.api.Language;

public class JavaLanguage implements Language {
    private final JavaCodeFormatter myFormatter;
    private final JavaCodeHighlighter myHighlighter;
    private final JavaCodeAnalyzer myAnalyzer;
    private final JavaCodeCompleter myCompleter;
    private final JavaCodeNavigation myNavigation;
    private final JavaCodeRefactor myRefactor;
    public JavaLanguage(){
        myFormatter=new JavaCodeFormatter(this);
        myHighlighter=new JavaCodeHighlighter(this);
        myAnalyzer=new JavaCodeAnalyzer(this);
        myCompleter=new JavaCodeCompleter(this);
        myNavigation=new JavaCodeNavigation(this);
        myRefactor=new JavaCodeRefactor(this);
    }
    @Override
    public void initialize() {
    
    }
    
    @Override
    public void configureAssembly(@NonNull Assembly assembly) {
    
    }
    
    @Override
    public void shutdown() {
    
    }
    
    @NonNull
    @Override
    public String getName() {
        return "Java";
    }
    
    @NonNull
    @Override
    public String[] getFilePatterns() {
        return new String[]{"*.java","*.class"};
    }
    
    @Nullable
    @Override
    public CodeFormatter getFormatter() {
        return myFormatter;
    }
    
    @Nullable
    @Override
    public CodeHighlighter getHighlighter() {
        return myHighlighter;
    }
    
    @Nullable
    @Override
    public CodeAnalyzer getAnalyzer() {
        return myAnalyzer;
    }
    
    @Nullable
    @Override
    public CodeCompleter getCompleter() {
        return myCompleter;
    }
    
    @Nullable
    @Override
    public CodeNavigation getNavigation() {
        return myNavigation;
    }
    
    @Nullable
    @Override
    public CodeRefactor getRefactor() {
        return myRefactor;
    }
}
