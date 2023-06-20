package com.apkide.language.impl.classfile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.language.api.Language;
import com.apkide.language.api.Highlighter;
import com.apkide.language.api.DefaultHighlighter;
import com.apkide.language.impl.java.JavaLexer;

public class ClassFileLanguage implements Language {
	@NonNull
	@Override
	public String getName() {
		return "Java Binary";
	}

	private Highlighter highlighter;

	@Nullable
	@Override
	public Highlighter getHighlighter() {
		if (highlighter == null)
			highlighter = new DefaultHighlighter(new JavaLexer());
		return highlighter;
	}

	@NonNull
	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{"*.class"};
	}
}
