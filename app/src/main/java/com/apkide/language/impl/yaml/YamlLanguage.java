package com.apkide.language.impl.yaml;

import androidx.annotation.NonNull;

import com.apkide.language.api.CommonLanguage;
import com.apkide.language.api.Highlighter;

public class YamlLanguage extends CommonLanguage {
	@NonNull
	@Override
	public String getName() {
		return "YAML";
	}
	
	private YamlHighlighter highlighter;
	
	@NonNull
	@Override
	public Highlighter getHighlighter() {
		if (highlighter == null)
			highlighter = new YamlHighlighter();
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
