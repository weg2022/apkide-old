package com.apktool.cl.api.engine;

import com.apktool.cl.api.engine.highlighting.HighlightingStyle;
import com.apktool.cl.api.engine.highlighting.HighlightingTokenInfo;
import com.apktool.cl.api.engine.highlighting.TokenIterator;

import java.io.Reader;

public interface Highlighter {
    byte getDefaultLineStartState();

    byte highlightLine(Reader reader, byte initialState, TokenIterator iterator);

    int getStyleCount();

    boolean allowsCompletion(int tokenValue);

    HighlightingTokenInfo[] getTokenInfos();

    HighlightingStyle[] getDefaultStyles();

    boolean isHyperlinkIdentifierStyle(byte tokenValue);

    byte getSemanticHighlightingUnIfedLineStyle();

    byte getSemanticHighlightingTypeStyle();

    byte getSemanticHighlightingDelegateStyle();

    byte getSemanticHighlightingNamespaceStyle();

    byte getSemanticHighlightingKeywordStyle();
}
