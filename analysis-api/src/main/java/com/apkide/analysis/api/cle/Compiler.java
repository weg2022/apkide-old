package com.apkide.analysis.api.cle;

import com.apkide.analysis.api.clm.SyntaxTree;

public interface Compiler {
    void compile(SyntaxTree ast, boolean createDebugMetadata);
}
