package com.apkide.codemodel.api.language;

import android.annotation.Nullable;

import androidx.annotation.NonNull;

public interface Language {
	
	@NonNull
	String getName();
	
	void shrink();
	
	@Nullable
	ArchiveReader getArchiveReader();
	
	@Nullable
	PreProcessor getPreProcessor();
	
	@Nullable
	Parser getParser();
	
	@Nullable
	Syntax getSyntax();
	
	@Nullable
	SignatureAnalyzer getSignatureAnalyzer();
	
	@Nullable
	TypeSystem getTypeSystem();
	
	@Nullable
	CodeRenderer getCodeRenderer();
	
	@Nullable
	CodeAnalyzer getCodeAnalyzer();
	
	@Nullable
	Tools getTools();
	
	@Nullable
	Compiler getCompiler();
	
	@Nullable
	Debugger getDebugger();
}
