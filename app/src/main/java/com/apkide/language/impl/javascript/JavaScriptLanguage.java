package com.apkide.language.impl.javascript;

import androidx.annotation.NonNull;

import com.apkide.language.api.CommonLanguage;
import com.apkide.language.api.Highlighter;

public class JavaScriptLanguage extends CommonLanguage {
	private JavaScriptHighlighter highlighter;
	
	@NonNull
	@Override
	public String getName() {
		return "JS";
	}
	
	@NonNull
	@Override
	public Highlighter getHighlighter() {
		if (highlighter == null)
			highlighter = new JavaScriptHighlighter();
		return highlighter;
	}
	
	@NonNull
	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{".js"};
	}
}
