package com.apkide.language.impl.javaclass;

import androidx.annotation.NonNull;

import com.apkide.language.api.CommonLanguage;
import com.apkide.language.api.Highlighter;
import com.apkide.language.impl.java.JavaHighlighter;

public class JavaClassLanguage extends CommonLanguage {
	@NonNull
	@Override
	public String getName() {
		return "Java-Class";
	}
	
	private JavaHighlighter highlighter;
	
	@NonNull
	@Override
	public Highlighter getHighlighter() {
		if (highlighter == null)
			highlighter = new JavaHighlighter();
		return highlighter;
	}
	
	@NonNull
	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{"*.class"};
	}
}
