package com.apkide.analysis.cle;

import com.apkide.analysis.cle.api.parenmatcher.ParenMatchResult;
import com.apkide.analysis.cle.api.parenmatcher.ParenMatcherEditor;

public interface ParenMatcher {
   void findMatchingParen(ParenMatcherEditor editor, int caretLine, int caretColumn, ParenMatchResult result);

   boolean isParenChar(char c);
}
