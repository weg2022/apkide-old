package com.apkide.language.impl.java;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.language.api.CommonLanguage;
import com.apkide.language.api.Highlighter;
import com.apkide.language.api.HighlighterProxy;

public class JavaLanguage extends CommonLanguage {
	@NonNull
	@Override
	public String getName() {
		return "Java";
	}
	
	private Highlighter highlighter;
	
	@Nullable
	@Override
	public Highlighter getHighlighter() {
		if (highlighter == null)
			highlighter = new HighlighterProxy(new JavaLexer());
		return highlighter;
	}
	
	@NonNull
	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{"*.java"};
	}
}
