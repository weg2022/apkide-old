package com.apkide.language.impl.cpp;

import com.apkide.common.AppLog;
import com.apkide.language.api.Highlighter;
import com.apkide.language.api.SyntaxKind;
import com.apkide.language.api.TokenIterator;

import java.io.IOException;
import java.io.Reader;

public class CppHighlighter implements Highlighter {
	private final CppLexer lexer = new CppLexer();
	
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
			case CppLexer.KEYWORD:
				return SyntaxKind.Keyword;
			case CppLexer.OPERATOR:
				return SyntaxKind.Operator;
			case CppLexer.SEPARATOR:
				return SyntaxKind.Separator;
			case CppLexer.LITERAL:
				return SyntaxKind.Literal;
			case CppLexer.TYPE:
				return SyntaxKind.TypeIdentifier;
			case CppLexer.PROCESSOR:
				return SyntaxKind.Preprocessor;
			case CppLexer.COMMENT:
				return SyntaxKind.Comment;
			case CppLexer.DOC_COMMENT:
				return SyntaxKind.DocComment;
			case CppLexer.PLAIN:
			default:
				return SyntaxKind.Plain;
		}
	}
}
