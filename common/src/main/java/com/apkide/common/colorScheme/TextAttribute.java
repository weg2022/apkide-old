package com.apkide.common.colorScheme;

public class TextAttribute implements Attribute{
	public int foreground;
	public int background;
	public int fontStyle;
	
	public TextAttribute(int foreground, int background, int fontStyle) {
		this.foreground = foreground;
		this.background = background;
		this.fontStyle = fontStyle;
	}
	
	
}
