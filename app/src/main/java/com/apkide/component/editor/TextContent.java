package com.apkide.component.editor;

public interface TextContent {
	
	void setText(String text);
	
	void addTextChangeListener(TextChangeListener listener);
	
	void removeTextChangeListener(TextChangeListener listener);
	
	void replaceTextRange(int start, int replaceLength, String text);
	
	int getLineLength(int lineIndex);
	
	int getLinePhysicalLength(int lineIndex);
	
	String getLine(int lineIndex);
	
	String getLinePhysical(int lineIndex);
	
	int getLineAtOffset(int offset);
	
	int getLineCount();
	
	String getPlatformLineDelimiter();
	
	String getLineDelimiter();
	
	String getLineDelimiter(int lineIndex);
	
	int getOffsetAtLine(int lineIndex);
	
	char getChar(int offset);
	
	String getTextRange(int start, int length);
	
	String getText();
	
	int getCharCount();
}
