package com.apkide.language.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.common.AppLog;

import java.io.IOException;
import java.io.Reader;

public interface Highlighter {
	@Nullable
	Lexer getLexer();
	
	default int highlightLine(@NonNull Reader reader, int state, @NonNull TokenIterator iterator) {
		Lexer lexer = getLexer();
		if (lexer == null) return 0;
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
	
	int getDefaultState();
	
	@NonNull
	default SyntaxKind getSyntaxKind(int type) {
		if (getLexer() == null) return SyntaxKind.Plain;
		switch (type) {
			case Lexer.Keyword:
				return SyntaxKind.Keyword;
			case Lexer.Operator:
				return SyntaxKind.Operator;
			case Lexer.Separator:
				return SyntaxKind.Separator;
			case Lexer.Literal:
				return SyntaxKind.Literal;
			case Lexer.Preprocessor:
				return SyntaxKind.Preprocessor;
			case Lexer.Identifier:
				return SyntaxKind.Identifier;
			case Lexer.NamespaceIdentifier:
				return SyntaxKind.NamespaceIdentifier;
			case Lexer.TypeIdentifier:
				return SyntaxKind.TypeIdentifier;
			case Lexer.DelegateIdentifier:
				return SyntaxKind.DelegateIdentifier;
			case Lexer.Comment:
				return SyntaxKind.Comment;
			case Lexer.DocComment:
				return SyntaxKind.DocComment;
			case Lexer.Plain:
			default:
				return SyntaxKind.Plain;
		}
	}
}
