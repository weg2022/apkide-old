package com.apkide.analysis.clm;

import com.apkide.analysis.clm.api.SyntaxTree;

public interface Compiler {
   void compile(SyntaxTree ast, boolean createDebugMetadata);
}
