package com.apkide.language.api;

public interface TokenIterator {
	void tokenFound(int type, int line, int column,int length);
}
