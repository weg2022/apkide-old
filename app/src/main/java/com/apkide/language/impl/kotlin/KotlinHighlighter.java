package com.apkide.language.impl.kotlin;

import com.apkide.common.AppLog;
import com.apkide.language.api.Highlighter;
import com.apkide.language.api.SyntaxKind;
import com.apkide.language.api.TokenIterator;

import java.io.IOException;
import java.io.Reader;

public class KotlinHighlighter implements Highlighter {
	private final KotlinLexer lexer = new KotlinLexer();
	
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
			case KotlinLexer.KEYWORD:
				return SyntaxKind.Keyword;
			case KotlinLexer.OPERATOR:
				return SyntaxKind.Operator;
			case KotlinLexer.SEPARATOR:
				return SyntaxKind.Separator;
			case KotlinLexer.LITERAL:
				return SyntaxKind.Literal;
			case KotlinLexer.TYPE:
				return SyntaxKind.TypeIdentifier;
			case KotlinLexer.COMMENT:
				return SyntaxKind.Comment;
			case KotlinLexer.DOC_COMMENT:
				return SyntaxKind.DocComment;
			case KotlinLexer.PLAIN:
			default:
				return SyntaxKind.Plain;
		}
	}
}
