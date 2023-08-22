package com.apkide.language.yaml;

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

public class YamlLanguage implements Language {
    private final YamlCodeFormatter myFormatter;
    private final YamlCodeHighlighter myHighlighter;
    private final YamlCodeAnalyzer myAnalyzer;
    private final YamlCodeCompleter myCompleter;
    private final YamlCodeNavigation myNavigation;
    private final YamlCodeRefactor myRefactor;
    public YamlLanguage(){
        myFormatter=new YamlCodeFormatter(this);
        myHighlighter=new YamlCodeHighlighter(this);
        myAnalyzer=new YamlCodeAnalyzer(this);
        myCompleter=new YamlCodeCompleter(this);
        myNavigation=new YamlCodeNavigation(this);
        myRefactor=new YamlCodeRefactor(this);
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
        return "Yaml";
    }
    
    @NonNull
    @Override
    public String[] getFilePatterns() {
        return new String[]{"*.yaml","*.yml"};
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
