package com.apkide.language.impl.log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.language.api.CommonLanguage;
import com.apkide.language.api.Highlighter;
import com.apkide.language.api.HighlighterProxy;

public class LogLanguage extends CommonLanguage {
	@NonNull
	@Override
	public String getName() {
		return "LOG";
	}

	private Highlighter highlighter;

	@Nullable
	@Override
	public Highlighter getHighlighter() {
		if (highlighter == null) {
			highlighter = new HighlighterProxy(new LogLexer());
		}
		return highlighter;
	}

	@NonNull
	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{"*.apkidelog"};
	}
}
