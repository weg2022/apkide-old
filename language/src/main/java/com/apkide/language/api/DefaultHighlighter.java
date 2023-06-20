package com.apkide.language.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DefaultHighlighter implements Highlighter {
	private final Lexer lexer;
	
	public DefaultHighlighter(@NonNull Lexer lexer) {
		this.lexer = lexer;
	}
	
	@Nullable
	@Override
	public Lexer getLexer() {
		return lexer;
	}
	
	@Override
	public int getDefaultState() {
		return lexer.getDefaultState();
	}
}
