package com.apkide.language.smali;

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

public class SmaliLanguage implements Language {
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
        return "Smali";
    }
    
    @NonNull
    @Override
    public String[] getFilePatterns() {
        return new String[]{"*.smali"};
    }
    
    @Nullable
    @Override
    public CodeFormatter getFormatter() {
        return null;
    }
    
    @Nullable
    @Override
    public CodeHighlighter getHighlighter() {
        return null;
    }
    
    @Nullable
    @Override
    public CodeAnalyzer getAnalyzer() {
        return null;
    }
    
    @Nullable
    @Override
    public CodeCompleter getCompleter() {
        return null;
    }
    
    @Nullable
    @Override
    public CodeNavigation getNavigation() {
        return null;
    }
    
    @Nullable
    @Override
    public CodeRefactor getRefactor() {
        return null;
    }
}
