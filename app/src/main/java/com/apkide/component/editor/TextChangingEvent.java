package com.apkide.component.editor;

import java.util.EventObject;

public class TextChangingEvent extends EventObject {
	public int start;
	
	public String newText;
	
	public int replaceCharCount;
	
	public int newCharCount;
	
	public int replaceLineCount;
	
	public int newLineCount;
	
	public TextChangingEvent(TextContent content) {
		super(content);
	}
}
