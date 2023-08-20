package com.apkide.ls.java;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.ls.api.CodeCompiler;
import com.apkide.ls.api.CodeCompleter;
import com.apkide.ls.api.CodeFormatter;
import com.apkide.ls.api.CodeHighlighter;
import com.apkide.ls.api.CodeNavigation;
import com.apkide.ls.api.CodeRefactor;
import com.apkide.ls.api.LanguageServer;
import com.apkide.ls.api.Model;

public class JavaLanguageServer implements LanguageServer {
    private Model myModel;
    
    @Override
    public void initialize(@NonNull Model model) {
        myModel = model;
    }
    
    @Override
    public void shutdown() {
    
    }
    
    protected Model getModel() {
        return myModel;
    }
    
    @Override
    public void configureRootPah(@NonNull String rootPath) {
    
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
    
    @Nullable
    @Override
    public CodeCompleter getCompleter() {
        return null;
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
    public CodeNavigation getNavigation() {
        return null;
    }
    
    @Nullable
    @Override
    public CodeRefactor getRefactor() {
        return null;
    }
    
    @Nullable
    @Override
    public CodeCompiler getCompiler() {
        return null;
    }
}
