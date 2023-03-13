package com.apkide.language.impl.groovy;

import com.apkide.common.AppLog;
import com.apkide.language.api.Highlighter;
import com.apkide.language.api.SyntaxKind;
import com.apkide.language.api.TokenIterator;

import java.io.IOException;
import java.io.Reader;

public class GroovyHighlighter implements Highlighter {
	private final GroovyLexer lexer = new GroovyLexer();
	
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
			case GroovyLexer.KEYWORD:
				return SyntaxKind.Keyword;
			case GroovyLexer.OPERATOR:
				return SyntaxKind.Operator;
			case GroovyLexer.SEPARATOR:
				return SyntaxKind.Separator;
			case GroovyLexer.LITERAL:
				return SyntaxKind.Literal;
			case GroovyLexer.TYPE:
				return SyntaxKind.TypeIdentifier;
			case GroovyLexer.COMMENT:
				return SyntaxKind.Comment;
			case GroovyLexer.DOC_COMMENT:
				return SyntaxKind.DocComment;
			case GroovyLexer.PLAIN:
			default:
				return SyntaxKind.Plain;
		}
	}
}
