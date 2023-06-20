package com.apkide.language.impl.smali;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.language.api.Language;
import com.apkide.language.api.Highlighter;

public class SmaliLanguage implements Language {
	
	private SmaliHighlighter highlighter;
	
	@NonNull
	@Override
	public String getName() {
		return "Smali";
	}
	
	@Nullable
	@Override
	public Highlighter getHighlighter() {
		if (highlighter == null)
			highlighter = new SmaliHighlighter();
		return highlighter;
	}
	
	@NonNull
	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{"*.smali"};
	}
}
