package com.apkide.ls.api.lexer;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.Reader;

public interface Lexer {
    int PlainStyle = 0;
    int KeywordStyle = 1;
    int OperatorStyle = 2;
    int SeparatorStyle = 3;
    int StringStyle = 4;
    int NumberStyle = 5;
    int MetadataStyle = 6;
    int IdentifierStyle = 7;
    int NamespaceStyle = 8;
    int TypeStyle = 9;
    int VariableStyle = 10;
    int FunctionStyle = 11;
    int FunctionCallStyle = 12;
    int ParameterStyle = 13;
    int CommentStyle = 14;
    int BlockCommentStyle = 15;
    
    void reset(@NonNull Reader reader);
    
    void setState(int state);
    
    int getState();
    
    int getDefaultState();
    
    int nextToken() throws IOException;
    
    boolean atEOF();
    
    int getLine();
    
    int getColumn();
    
}
