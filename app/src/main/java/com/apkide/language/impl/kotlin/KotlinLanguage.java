package com.apkide.language.impl.kotlin;

import androidx.annotation.NonNull;

import com.apkide.language.api.CommonLanguage;
import com.apkide.language.api.Highlighter;

public class KotlinLanguage extends CommonLanguage {
	@NonNull
	@Override
	public String getName() {
		return "Kotlin";
	}
	
	private KotlinHighlighter highlighter;
	
	@NonNull
	@Override
	public Highlighter getHighlighter() {
		if (highlighter == null)
			highlighter = new KotlinHighlighter();
		return highlighter;
	}
	
	@NonNull
	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{"*.kt", "*.kts"};
	}
}
