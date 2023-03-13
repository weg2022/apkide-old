package com.apkide.language.impl.javascript;

import com.apkide.common.AppLog;
import com.apkide.language.api.Highlighter;
import com.apkide.language.api.SyntaxKind;
import com.apkide.language.api.TokenIterator;

import java.io.IOException;
import java.io.Reader;

public class JavaScriptHighlighter implements Highlighter {
	private final JavaScriptLexer lexer = new JavaScriptLexer();
	
	@Override
	public int highlightLine(Reader reader, int state, TokenIterator iterator) {
		try {
			lexer.reset(reader);
			lexer.setState(state);
			while (true) {
				int token = lexer.nextToken();
				if (token == -1) break;
				iterator.tokenFound(token, lexer.getLine(), lexer.getColumn());
			}
			return lexer.getState();
		} catch (IOException e) {
			AppLog.e(e);
		}
		return getDefaultState();
	}
	
	@Override
	public int getDefaultState() {
		return lexer.getDefaultState();
	}
	
	@Override
	public SyntaxKind getSyntaxKind(int token) {
		switch (token) {
			case JavaScriptLexer.KEYWORD:
				return SyntaxKind.Keyword;
			case JavaScriptLexer.OPERATOR:
				return SyntaxKind.Operator;
			case JavaScriptLexer.SEPARATOR:
				return SyntaxKind.Separator;
			case JavaScriptLexer.LITERAL:
				return SyntaxKind.Literal;
			case JavaScriptLexer.COMMENT:
				return SyntaxKind.Comment;
			case JavaScriptLexer.PLAIN:
			default:
				return SyntaxKind.Plain;
		}
	}
}
