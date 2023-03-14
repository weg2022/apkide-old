package com.apkide.language.impl.json5;

import androidx.annotation.NonNull;

import com.apkide.language.api.CommonLanguage;
import com.apkide.language.api.Highlighter;

public class Json5Language extends CommonLanguage {
	@NonNull
	@Override
	public String getName() {
		return "JSON5";
	}
	
	private Json5Highlighter highlighter;
	
	@NonNull
	@Override
	public Highlighter getHighlighter() {
		if (highlighter == null)
			highlighter = new Json5Highlighter();
		return highlighter;
	}
	
	@NonNull
	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{"*.json5"};
	}
}
