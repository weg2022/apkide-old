package com.apkide.language.impl.smali;

import androidx.annotation.Nullable;

import com.apkide.language.api.Highlighter;
import com.apkide.language.api.Lexer;

public class SmaliHighlighter implements Highlighter {
	private SmaliLexer lexer;

	@Nullable
	@Override
	public Lexer getLexer() {
		if (lexer == null)
			lexer = new SmaliLexer();
		return lexer;
	}

	@Override
	public int getDefaultState() {
		if (lexer == null)
			lexer = new SmaliLexer();
		return lexer.getDefaultState();
	}

}
