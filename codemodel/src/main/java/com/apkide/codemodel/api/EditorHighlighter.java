package com.apkide.codemodel.api;

import com.apkide.codemodel.api.highlighter.TokenIterator;

import java.io.Reader;

public interface EditorHighlighter {
	
	int highlightingLine(Reader reader, int state, TokenIterator iterator);
	
}
