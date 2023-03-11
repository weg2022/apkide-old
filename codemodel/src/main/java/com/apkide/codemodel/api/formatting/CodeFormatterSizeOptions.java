package com.apkide.codemodel.api.formatting;

public class CodeFormatterSizeOptions {
	public int indentationSize;
	public int tabSize;
	public int maxLinWidth;
	
	public CodeFormatterSizeOptions(){
		setDefaults();
	}

	public void setDefaults() {
		indentationSize=4;
		tabSize=4;
		maxLinWidth=100;
	}
	
}
