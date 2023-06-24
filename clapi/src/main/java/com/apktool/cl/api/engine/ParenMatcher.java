package com.apktool.cl.api.engine;


import com.apktool.cl.api.engine.parenmatcher.ParenMatchResult;
import com.apktool.cl.api.engine.parenmatcher.ParenMatcherEditor;

public interface ParenMatcher {
    void findMatchingParen(ParenMatcherEditor model, int caretLine, int caretColumn, ParenMatchResult result);

    boolean isParenChar(char c);
}
