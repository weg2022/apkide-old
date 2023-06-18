package com.apkide.language.impl.yaml;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.language.api.Language;
import com.apkide.language.api.Highlighter;
import com.apkide.language.api.HighlighterProxy;

public class YamlLanguage extends Language {
	@NonNull
	@Override
	public String getName() {
		return "YAML";
	}
	
	private Highlighter highlighter;
	
	@Nullable
	@Override
	public Highlighter getHighlighter() {
		if (highlighter == null)
			highlighter = new HighlighterProxy(new YamlLexer());
		return highlighter;
	}
	
	@NonNull
	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{
				"*.apinotes",
				"*.yml",
				"*.yaml",};
	}
}
