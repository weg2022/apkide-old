package com.apkide.codemodel.api;

import com.apkide.codemodel.api.parentMatch.ParenMatchResult;
import com.apkide.codemodel.api.parentMatch.ParenMatcherEditor;

public interface EditorParenMatcher {
	
	void findMatchingParen(ParenMatcherEditor model, int caretLine, int caretColumn, ParenMatchResult result);
	
	boolean isParenChar(char c);
}
