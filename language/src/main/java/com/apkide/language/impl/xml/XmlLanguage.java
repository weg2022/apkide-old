package com.apkide.language.impl.xml;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.language.api.Language;
import com.apkide.language.api.Highlighter;
import com.apkide.language.api.DefaultHighlighter;

public class XmlLanguage implements Language {
	private Highlighter highlighter;
	
	@NonNull
	@Override
	public String getName() {
		return "XML";
	}
	
	@Nullable
	@Override
	public Highlighter getHighlighter() {
		if (highlighter == null)
			highlighter = new DefaultHighlighter(new XmlLexer());
		return highlighter;
	}
	
	@NonNull
	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{
				"*.ant",
				"*.fxml",
				"*.jhm",
				"*.jnlp",
				"*.jrxml",
				"*.rng",
				"*.tld",
				"*.wsdl",
				"*.xml",
				"*.xsd",
				"*.xsl",
				"*.xslt",
				"*.xul"};
	}
}
