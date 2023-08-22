package com.apkide.language.xml;

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

public class XmlLanguage implements Language {
    private final XmlCodeFormatter myFormatter;
    private final XmlCodeHighlighter myHighlighter;
    private final XmlCodeAnalyzer myAnalyzer;
    private final XmlCodeCompleter myCompleter;
    private final XmlCodeNavigation myNavigation;
    private final XmlCodeRefactor myRefactor;
    
    public XmlLanguage() {
        myFormatter = new XmlCodeFormatter(this);
        myHighlighter = new XmlCodeHighlighter(this);
        myAnalyzer = new XmlCodeAnalyzer(this);
        myCompleter = new XmlCodeCompleter(this);
        myNavigation = new XmlCodeNavigation(this);
        myRefactor = new XmlCodeRefactor(this);
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
        return "Xml";
    }
    
    @NonNull
    @Override
    public String[] getFilePatterns() {
        return new String[]{"*.xml", "*.classpath", "*.pom", "*.imi"};
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
