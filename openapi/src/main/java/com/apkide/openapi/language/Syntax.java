package com.apkide.openapi.language;

import androidx.annotation.NonNull;

public interface Syntax {
    boolean isParameters(int token);

    boolean isMethodDeclaration(int token);

    boolean isFieldDeclaration(int token);

    boolean isArguments(int token);

    boolean isClassBody(int token);

    boolean isBlock(int token);

    boolean isIdentifier(int token);

    boolean isExpression(int token);

    boolean isStatement(int token);

    boolean isMemberDeclaration(int token);

    boolean isClassDeclaration(int token);

    boolean isLiteral(int token);

    boolean isToken(int token);

    @NonNull
    String getString(int token);

    int getLength(int token);
}
