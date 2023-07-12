package com.apkide.analysis.api.clm.util;

import java.io.IOException;
import java.io.Reader;

public interface JFlexLexer {

    void yyreset(Reader reader);

    void yybegin(int newState);

    boolean yyatEOF();

    int yylex()throws IOException;

    int yystate();

    int yylength();

    String yytext();

    void yypushback(int number);

    char yycharat(int position);

    char[] getBuffer();

    int getBufferIndex();

    int getCharIndex();

    void yyclose()throws IOException;
}
