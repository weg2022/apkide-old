package com.apkide.codemodel.api.parentMatch;

public interface ParenMatcherEditor {
	int getLineCount();
	int getLineWidth(int line);
	
	char getChar(int line, int column);
	
	int getStyle(int line, int column);
	
	
}
