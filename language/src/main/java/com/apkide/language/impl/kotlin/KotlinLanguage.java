package com.apkide.language.impl.kotlin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.language.api.Language;
import com.apkide.language.api.Highlighter;
import com.apkide.language.api.DefaultHighlighter;

public class KotlinLanguage implements Language {
	@NonNull
	@Override
	public String getName() {
		return "Kotlin";
	}
	
	private Highlighter highlighter;
	
	@Nullable
	@Override
	public Highlighter getHighlighter() {
		if (highlighter == null)
			highlighter = new DefaultHighlighter(new KotlinLexer());
		return highlighter;
	}
	
	@NonNull
	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{"*.kt", "*.kts"};
	}
}
