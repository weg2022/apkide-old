package com.apkide.language.impl.java;

import com.apkide.common.AppLog;
import com.apkide.language.runtime.Highlighter;
import com.apkide.language.runtime.TokenIterator;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

import java.io.IOException;
import java.io.Reader;

import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class JavaHighlighter implements Highlighter {
    private JavaLexer lexer = new JavaLexer(null);
    private JavaParser parser = new JavaParser(null);

    @Override
    public void highlighting(Reader reader, TokenIterator iterator) {
        try {
            lexer.reset();
            lexer.setInputStream(CharStreams.fromReader(reader));
            CommonTokenStream tokenStream=new CommonTokenStream(lexer);
            tokenStream.fill();
            for (Token token : tokenStream.getTokens()) {
                int id = token.getType();
                int lineIndex = token.getLine();
                int column = token.getCharPositionInLine();
                if (id >= 1 && id <= 66 || id == 73 || id == 77) {
                    iterator.tokenFound((byte) EditorColorScheme.KEYWORD, lineIndex, column);
                } else if (id >= 67 && id <= 72) {
                    iterator.tokenFound((byte) EditorColorScheme.LITERAL, lineIndex, column);
                } else if (id >= 74 && id <= 76) {
                    iterator.tokenFound((byte) EditorColorScheme.LITERAL, lineIndex, column);
                } else if (id >= 84 && id <= 122) {
                    iterator.tokenFound((byte) EditorColorScheme.OPERATOR, lineIndex, column);
                } else if (id == 126|| id==127) {
                    iterator.tokenFound((byte) EditorColorScheme.COMMENT,lineIndex, column);
                }
            }

        } catch (IOException e) {
            AppLog.e(e);
        }
    }
}
