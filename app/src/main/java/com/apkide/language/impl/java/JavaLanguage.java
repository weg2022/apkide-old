package com.apkide.language.impl.java;

import androidx.annotation.NonNull;

import com.apkide.language.api.CommonLanguage;
import com.apkide.language.api.Highlighter;

public class JavaLanguage extends CommonLanguage {
	private JavaHighlighter highlighter;
	
	@NonNull
	@Override
	public String getName() {
		return "Java";
	}
	
	@NonNull
	@Override
	public Highlighter getHighlighter() {
		if (highlighter == null)
			highlighter = new JavaHighlighter();
		return highlighter;
	}
	
	@NonNull
	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{"*.java", "*.class"};
	}
}
