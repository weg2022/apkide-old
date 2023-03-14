package com.apkide.language.impl.xml;

import androidx.annotation.NonNull;

import com.apkide.language.api.CommonLanguage;
import com.apkide.language.api.Highlighter;

public class XmlLanguage extends CommonLanguage {
	private XmlHighlighter highlighter;
	
	@NonNull
	@Override
	public String[] getDefaultFilePatterns() {
		return new String[] {
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
	
	@NonNull
	@Override
	public String getName() {
		return "XML";
	}
	
	@NonNull
	@Override
	public Highlighter getHighlighter() {
		if (highlighter == null)
			highlighter = new XmlHighlighter();
		return highlighter;
	}
}
