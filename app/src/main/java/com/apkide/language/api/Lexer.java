package com.apkide.language.api;

import java.io.IOException;
import java.io.Reader;

public interface Lexer {
	int Plain = 0;
	int Keyword = 1;
	int Operator = 2;
	int Separator = 3;
	int Literal = 4;
	int Preprocessor = 5;
	int Identifier = 6;
	int NamespaceIdentifier = 7;
	int TypeIdentifier = 8;
	int DelegateIdentifier = 9;
	int Comment = 10;
	int DocComment = 11;
	
	void setReader(Reader reader);
	
	void reset(Reader reader);
	
	void setState(int state);
	
	int getState();
	
	int getDefaultState();
	
	int nextToken()throws IOException;
	
	int getLine();
	
	int getColumn();
}
