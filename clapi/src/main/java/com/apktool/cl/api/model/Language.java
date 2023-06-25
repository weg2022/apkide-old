package com.apktool.cl.api.model;


public interface Language {
    String getName();

    void shrink();

    ArchiveReader getArchiveReader();

    Preprocessor getPreProcessor();

    Parser getParser();

    Syntax getSyntax();

    SignatureAnalyzer getSignatureAnalyzer();

    TypeSystem getTypeSystem();

    CodeRenderer getRenderer();

    CodeAnalyzer getCodeAnalyzer();

    Tools getTools();

    Compiler getCompiler();
}
