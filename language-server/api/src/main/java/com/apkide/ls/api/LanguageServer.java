package com.apkide.ls.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface LanguageServer {
    
    
    void initialize(@NonNull Model model);
    
    void shutdown();
    
    void configureRootPah(@NonNull String rootPath);
    
    @NonNull
    String getName();
    
    @NonNull
    String[] getDefaultFilePatterns();
    
    
    @Nullable
    CodeCompleter getCompleter();
    
    @Nullable
    CodeFormatter getFormatter();
    
    @Nullable
    CodeHighlighter getHighlighter();
    
    @Nullable
    CodeNavigation getNavigation();
    
    @Nullable
    CodeRefactor getRefactor();
    
    @Nullable
    CodeCompiler getCompiler();
}
