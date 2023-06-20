package com.apkide.language.impl.java;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.language.api.Language;
import com.apkide.language.api.Highlighter;
import com.apkide.language.api.DefaultHighlighter;

public class JavaLanguage implements Language {
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
			highlighter = new DefaultHighlighter(new JavaLexer());
		return highlighter;
	}
	
	@NonNull
	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{"*.java"};
	}
}
