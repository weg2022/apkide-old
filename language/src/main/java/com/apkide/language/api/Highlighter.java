package com.apkide.language.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.common.AppLog;

import java.io.IOException;
import java.io.Reader;

public interface Highlighter {
    @Nullable
    Lexer getLexer();

    default int highlightLine(@NonNull Reader reader, int state, @NonNull TokenIterator iterator) {
        Lexer lexer = getLexer();
        if (lexer == null) return 0;
        try {
            lexer.reset(reader);
            lexer.setState(state);
            while (true) {
                int token = lexer.nextToken();
                iterator.tokenFound(token, lexer.getLine(), lexer.getColumn(),lexer.yylength());
                if (token == -1) break;
            }
            return lexer.getState();
        } catch (IOException e) {
            AppLog.e(e);
        }
        return getDefaultState();
    }

    int getDefaultState();

    default int getType(int token) {
        return token;
    }
}
