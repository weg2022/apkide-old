package com.apkide.codemodel.api.language;

import com.apkide.codemodel.api.SyntaxTree;

public interface Compiler {
	void compile(SyntaxTree ast, boolean createDebugMetadata);
}
