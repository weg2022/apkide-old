package com.apkide.language.impl.cpp;

import androidx.annotation.NonNull;

import com.apkide.language.api.CommonLanguage;
import com.apkide.language.api.Highlighter;

public class CppLanguage extends CommonLanguage {
	private CppHighlighter highlighter;
	
	@NonNull
	@Override
	public Highlighter getHighlighter() {
		if (highlighter == null)
			highlighter = new CppHighlighter();
		return highlighter;
	}
	
	@NonNull
	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{"*.c", "*.h", "*.cpp", "*.hpp"};
	}
	
}
