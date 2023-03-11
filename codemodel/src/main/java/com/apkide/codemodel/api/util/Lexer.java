package com.apkide.codemodel.api.util;

import androidx.annotation.NonNull;

import java.io.Reader;

public interface Lexer {
	
	void setReader(@NonNull Reader reader);
	
	int getDefaultState();
	
	void setState(int state);
	
	int getState();
	
	int nextToken();
	
	int getTokenStart();
	
	int getTokenLength();
	
	int getTokenEnd();
	
	String getTokenText();
	
	boolean atEOF();
	
}
