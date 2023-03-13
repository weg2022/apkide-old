package com.apkide.language.impl.java;

import com.apkide.common.AppLog;
import com.apkide.language.api.Highlighter;
import com.apkide.language.api.SyntaxKind;
import com.apkide.language.api.TokenIterator;

import java.io.IOException;
import java.io.Reader;

public class JavaHighlighter implements Highlighter {
	private final JavaLexer lexer = new JavaLexer();
	
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
			case JavaLexer.KEYWORD:
				return SyntaxKind.Keyword;
			case JavaLexer.OPERATOR:
				return SyntaxKind.Operator;
			case JavaLexer.SEPARATOR:
				return SyntaxKind.Separator;
			case JavaLexer.LITERAL:
				return SyntaxKind.Literal;
			case JavaLexer.TYPE:
				return SyntaxKind.TypeIdentifier;
			case JavaLexer.COMMENT:
				return SyntaxKind.Comment;
			case JavaLexer.DOC_COMMENT:
				return SyntaxKind.DocComment;
			case JavaLexer.PLAIN:
			default:
				return SyntaxKind.Plain;
		}
	}
}
