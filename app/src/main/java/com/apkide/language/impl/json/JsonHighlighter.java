package com.apkide.language.impl.json;

import com.apkide.common.AppLog;
import com.apkide.language.api.Highlighter;
import com.apkide.language.api.SyntaxKind;
import com.apkide.language.api.TokenIterator;

import java.io.IOException;
import java.io.Reader;

public class JsonHighlighter implements Highlighter {
	private final JsonLexer lexer = new JsonLexer();
	
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
			case JsonLexer.KEYWORD:
				return SyntaxKind.Keyword;
			case JsonLexer.OPERATOR:
				return SyntaxKind.Operator;
			case JsonLexer.SEPARATOR:
				return SyntaxKind.Separator;
			case JsonLexer.LITERAL:
				return SyntaxKind.Literal;
			case JsonLexer.TYPE:
				return SyntaxKind.TypeIdentifier;
			case JsonLexer.COMMENT:
				return SyntaxKind.Comment;
			case JsonLexer.DOC_COMMENT:
				return SyntaxKind.DocComment;
			case JsonLexer.PLAIN:
			default:
				return SyntaxKind.Plain;
		}
	}
}
