package com.apkide.language.api;

import java.io.IOException;
import java.io.Reader;

public interface Lexer {
	
	void setReader(Reader reader);
	
	void reset(Reader reader);
	
	void setState(int state);
	
	int getState();
	
	int getDefaultState();
	
	int nextToken()throws IOException;
	
	int getLine();
	
	int getColumn();
}
