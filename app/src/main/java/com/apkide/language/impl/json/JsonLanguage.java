package com.apkide.language.impl.json;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.language.api.CommonLanguage;
import com.apkide.language.api.Highlighter;
import com.apkide.language.api.HighlighterProxy;

public class JsonLanguage extends CommonLanguage {
	@NonNull
	@Override
	public String getName() {
		return "JSON5";
	}
	
	private Highlighter highlighter;
	
	@Nullable
	@Override
	public Highlighter getHighlighter() {
		if (highlighter == null)
			highlighter = new HighlighterProxy(new JsonLexer());
		return highlighter;
	}
	
	@NonNull
	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{
				"*.har",
				"*.json",
				"*.json5"
		};
	}
}
