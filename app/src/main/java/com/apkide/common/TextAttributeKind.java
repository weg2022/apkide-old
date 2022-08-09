package com.apkide.common;

public enum TextAttributeKind {
    Plain( 0),
    Hyperlink(1),
    Whitespace(2),

    Deprecated(10),
    Error(11),
    Warning(12),
    Typo(13),
    Unknown(14),
    Unused(15),

    Preprocessor( 20),
    Namespace(21),
    Keyword(22),
    StringLiteral(23),
    NumberLiteral(24),
    Operator( 25),
    Separator( 26),
    Type(27),
    Variable(28),
    Function(29),
    Parameter(30),
    Comment(31),
    DocComment(32)
    ;
    public final int id;

    TextAttributeKind(int id) {
        this.id = id;
    }
}
