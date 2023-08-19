package com.apkide.ls.api;

import com.apkide.ls.api.lexer.Lexer;

import java.io.IOException;
import java.io.Reader;

public class FileHighlighter {
    private final Model myModel;
    private final Lexer myLexer;
    
    public FileHighlighter(Model model, Lexer lexer) {
        myModel = model;
        myLexer = lexer;
    }
    
    public void highlighting(String filePath, Reader reader, Highlights highlights) {
        try {
            highlights.clear();
            myLexer.reset(reader);
            myLexer.setState(myLexer.getDefaultState());
            int style = myLexer.nextToken();
            int startColumn = myLexer.getLine();
            int startLine = myLexer.getColumn();
            
            while (true) {
                int token = myLexer.nextToken();
                int line = myLexer.getLine();
                int column = myLexer.getColumn();
                highlights.highlight(style, startLine, startColumn, line, column);
                style = token;
                startColumn = column;
                startLine = column;
                if (token == -1) break;
            }
        } catch (IOException ignored) {
        
        }
        myModel.getHighlighterCallback().fileHighlighting(filePath, 0, highlights);
    }
}
