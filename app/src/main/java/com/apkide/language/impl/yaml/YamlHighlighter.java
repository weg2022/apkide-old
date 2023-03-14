package com.apkide.language.impl.yaml;

import com.apkide.common.AppLog;
import com.apkide.language.api.Highlighter;
import com.apkide.language.api.SyntaxKind;
import com.apkide.language.api.TokenIterator;

import java.io.IOException;
import java.io.Reader;

public class YamlHighlighter implements Highlighter {
	private final YamlLexer lexer = new YamlLexer();
	
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
			case YamlLexer.KEYWORD:
				return SyntaxKind.Keyword;
			case YamlLexer.OPERATOR:
				return SyntaxKind.Operator;
			case YamlLexer.SEPARATOR:
				return SyntaxKind.Separator;
			case YamlLexer.LITERAL:
				return SyntaxKind.Literal;
			case YamlLexer.TYPE:
				return SyntaxKind.TypeIdentifier;
			case YamlLexer.COMMENT:
				return SyntaxKind.Comment;
			case YamlLexer.PLAIN:
			default:
				return SyntaxKind.Plain;
		}
	}
}
