package com.apkide.language.yaml;

import androidx.annotation.NonNull;

import com.apkide.language.api.CodeHighlighter;
import com.apkide.language.api.CodeHighlighterCallback;

import java.io.IOException;
import java.io.Reader;

public class YamlCodeHighlighter implements CodeHighlighter {
    private final YamlLanguage myLanguage;
    private final YamlLexer myLexer=new YamlLexer();
    public YamlCodeHighlighter(YamlLanguage language) {
        myLanguage = language;
    }
    
    @Override
    public void highlighting(@NonNull String filePath, @NonNull CodeHighlighterCallback callback) {
        try {
            Reader reader = callback.getFileReader(filePath);
            myLexer.yyreset(reader);
            myLexer.yybegin(myLexer.getDefaultState());
            int style = myLexer.yylex();
            int startLine = myLexer.getLine();
            int startColumn = myLexer.getColumn();
        
            while (true) {
                int nextStyle = myLexer.yylex();
                int line = myLexer.getLine();
                int column = myLexer.getColumn();
                callback.tokenFound(style, startLine,
                        startColumn, line, column);
                style = nextStyle;
                startLine = line;
                startColumn = column;
                if (nextStyle == -1) break;
            }
        } catch (IOException ignored) {
        
        }
    }
    
    @Override
    public void semanticHighlighting(@NonNull String filePath, @NonNull CodeHighlighterCallback callback) {
    
    }
}
