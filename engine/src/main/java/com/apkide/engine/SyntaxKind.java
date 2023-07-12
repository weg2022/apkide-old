package com.apkide.engine;

public enum SyntaxKind {
    Plain,
    Keyword,
    Operator,
    Separator,
    StringLiteral,
    NumberLiteral,
    Metadata,
    Identifier,
    NamespaceIdentifier,
    ClassIdentifier,
    TypeIdentifier,
    VariableIdentifier,
    FunctionIdentifier,
    ParameterIdentifier,
    Comment,
    DocumentationComment
    ;


    public byte toByte(){
        return (byte) ordinal();
    }
    public static SyntaxKind of(byte kind) {
        return values()[kind];
    }
}
