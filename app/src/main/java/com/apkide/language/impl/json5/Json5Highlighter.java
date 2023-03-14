package com.apkide.language.impl.json5;

import com.apkide.common.AppLog;
import com.apkide.language.api.Highlighter;
import com.apkide.language.api.SyntaxKind;
import com.apkide.language.api.TokenIterator;

import java.io.IOException;
import java.io.Reader;

public class Json5Highlighter implements Highlighter {
	private final Json5Lexer lexer = new Json5Lexer();
	
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
			case Json5Lexer.KEYWORD:
				return SyntaxKind.Keyword;
			case Json5Lexer.OPERATOR:
				return SyntaxKind.Operator;
			case Json5Lexer.SEPARATOR:
				return SyntaxKind.Separator;
			case Json5Lexer.LITERAL:
				return SyntaxKind.Literal;
			case Json5Lexer.TYPE:
				return SyntaxKind.TypeIdentifier;
			case Json5Lexer.COMMENT:
				return SyntaxKind.Comment;
			case Json5Lexer.DOC_COMMENT:
				return SyntaxKind.DocComment;
			case Json5Lexer.PLAIN:
			default:
				return SyntaxKind.Plain;
		}
	}
}
