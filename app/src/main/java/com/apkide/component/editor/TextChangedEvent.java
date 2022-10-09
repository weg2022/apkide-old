package com.apkide.component.editor;

import java.util.EventObject;

public class TextChangedEvent extends EventObject {
	
	public int start;
	
	public String newText;
	
	public int replaceCharCount;
	
	public int newCharCount;
	
	public int replaceLineCount;
	
	public int newLineCount;
	
	public TextChangedEvent(TextContent content) {
		super(content);
	}
}
