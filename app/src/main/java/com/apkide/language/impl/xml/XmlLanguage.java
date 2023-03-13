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
				"*.xml",
				"*.xsl",
				"*.xslt",
				"*.xsd",
				"*.*proj",
				"*.resx",
				"*.settings",
				"*.config",
				"*.html",
				"*.htm",
				"*.pom",
				"*.classpath",
				"*.imi"};
	}
	
	@NonNull
	@Override
	public Highlighter getHighlighter() {
		if (highlighter==null)
			highlighter=new XmlHighlighter();
		return highlighter;
	}
}
