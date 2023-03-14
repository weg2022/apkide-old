package com.apkide.language.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HighlighterProxy implements Highlighter {
	private final Lexer lexer;
	
	public HighlighterProxy(@NonNull Lexer lexer) {
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
