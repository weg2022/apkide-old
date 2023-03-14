package com.apkide.language.impl.json;

import androidx.annotation.NonNull;

import com.apkide.language.api.CommonLanguage;
import com.apkide.language.api.Highlighter;

public class JsonLanguage extends CommonLanguage {
	@NonNull
	@Override
	public String getName() {
		return "JSON";
	}
	
	private JsonHighlighter highlighter;
	
	@NonNull
	@Override
	public Highlighter getHighlighter() {
		if (highlighter == null)
			highlighter = new JsonHighlighter();
		return highlighter;
	}
	
	@NonNull
	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{
				"*.har",
				"*.json"
		};
	}
}
