package com.apkide.language.api;

import java.io.Reader;

public interface Highlighter {
	
	int highlightLine(Reader reader,int state,TokenIterator iterator);
	
	int getDefaultState();
	
	SyntaxKind getSyntaxKind(int token);
}
