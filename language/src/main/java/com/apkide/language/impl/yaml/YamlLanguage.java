package com.apkide.language.impl.yaml;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.language.api.Language;
import com.apkide.language.api.Highlighter;
import com.apkide.language.api.DefaultHighlighter;

public class YamlLanguage implements Language {
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
			highlighter = new DefaultHighlighter(new YamlLexer());
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
