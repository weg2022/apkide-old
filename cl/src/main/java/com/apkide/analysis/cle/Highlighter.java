package com.apkide.analysis.cle;

import com.apkide.analysis.cle.api.highlighter.HighlightingStyle;
import com.apkide.analysis.cle.api.highlighter.HighlightingTokenInfo;
import com.apkide.analysis.cle.api.highlighter.TokenIterator;
import java.io.Reader;

public interface Highlighter {
   byte getDefaultState();

   byte highlightLine(Reader reader, byte state, TokenIterator iterator);

   int getStyleCount();

   boolean allowsCompletion(int style);

   HighlightingTokenInfo[] getTokenInfos();

   HighlightingStyle[] getDefaultStyles();

   boolean isHyperlinkableIdentifierStyle(byte style);

   byte getSemanticHighlightingUnIfedLineStyle();

   byte getSemanticHighlightingTypeStyle();

   byte getSemanticHighlightingDelegateStyle();

   byte getSemanticHighlightingNamespaceStyle();

   byte getSemanticHighlightingKeywordStyle();
}
