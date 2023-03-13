package com.apkide.language.impl.javascript;

import androidx.annotation.NonNull;

import com.apkide.language.api.CommonLanguage;
import com.apkide.language.api.Highlighter;
import com.apkide.language.impl.java.JavaHighlighter;

public class JavaScriptLanguage extends CommonLanguage {
	private JavaScriptHighlighter highlighter;
	@NonNull
	@Override
	public Highlighter getHighlighter() {
		if (highlighter == null)
			highlighter=new JavaScriptHighlighter();
		return highlighter;
	}
	
	@NonNull
	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{".js"};
	}
}
