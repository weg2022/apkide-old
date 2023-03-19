package com.apkide.language.impl.log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.language.api.CommonLanguage;
import com.apkide.language.api.Highlighter;

public class LogLanguage extends CommonLanguage {
	@NonNull
	@Override
	public String getName() {
		return "LOG";
	}

	@Nullable
	@Override
	public Highlighter getHighlighter() {
		return null;
	}

	@NonNull
	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{"*.log", "*.logcat"};
	}
}
