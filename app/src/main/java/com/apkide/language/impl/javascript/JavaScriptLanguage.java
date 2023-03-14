package com.apkide.language.impl.javascript;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.language.api.CommonLanguage;
import com.apkide.language.api.Highlighter;
import com.apkide.language.api.HighlighterProxy;

public class JavaScriptLanguage extends CommonLanguage {
	@NonNull
	@Override
	public String getName() {
		return "JS";
	}
	
	private Highlighter highlighter;
	
	@Nullable
	@Override
	public Highlighter getHighlighter() {
		if (highlighter == null)
			highlighter = new HighlighterProxy(new JavaScriptLexer());
		return highlighter;
	}
	
	@NonNull
	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{".js"};
	}
}
