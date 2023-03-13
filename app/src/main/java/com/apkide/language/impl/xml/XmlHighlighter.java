package com.apkide.language.impl.xml;

import com.apkide.common.AppLog;
import com.apkide.language.api.Highlighter;
import com.apkide.language.api.SyntaxKind;
import com.apkide.language.api.TokenIterator;

import java.io.IOException;
import java.io.Reader;

public class XmlHighlighter implements Highlighter {
	private final XmlLexer lexer = new XmlLexer();
	
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
			case XmlLexer.KEYWORD:
				return SyntaxKind.Keyword;
			case XmlLexer.OPERATOR:
				return SyntaxKind.Operator;
			case XmlLexer.SEPARATOR:
				return SyntaxKind.Separator;
			case XmlLexer.LITERAL:
				return SyntaxKind.Literal;
			case XmlLexer.TYPE:
				return SyntaxKind.TypeIdentifier;
			case XmlLexer.DOC_COMMENT:
				return SyntaxKind.Comment;
			case XmlLexer.PLAIN:
			default:
				return SyntaxKind.Plain;
		}
	}
}
