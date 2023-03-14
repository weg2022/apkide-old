package com.apkide.language.impl.cpp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.language.api.CommonLanguage;
import com.apkide.language.api.Highlighter;
import com.apkide.language.api.HighlighterProxy;

public class CppLanguage extends CommonLanguage {
	@NonNull
	@Override
	public String getName() {
		return "Cpp";
	}
	
	private Highlighter highlighter;
	
	@Nullable
	@Override
	public Highlighter getHighlighter() {
		if (highlighter == null)
			highlighter = new HighlighterProxy(new CppLexer());
		return highlighter;
	}
	
	@NonNull
	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{
				"*.c",
				"*.c++",
				"*.cc",
				"*.cp",
				"*.cpp",
				"*.cppm",
				"*.cu",
				"*.cuh",
				"*.cxx",
				"*.h",
				"*.h++",
				"*.hh",
				"*.hp",
				"*.hpp",
				"*.hxx",
				"*.i",
				"*.icc",
				"*.ii",
				"*.inl",
				"*.ino",
				"*.ipp",
				"*.ixx",
				"*.m",
				"*.mm",
				"*.mxx",
				"*.pch",
				"*.tcc",
				"*.tpp"
		};
	}
	
}
