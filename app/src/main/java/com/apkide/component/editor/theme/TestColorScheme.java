package com.apkide.component.editor.theme;

import androidx.annotation.NonNull;

import com.apkide.component.editor.graphics.Color;

import java.util.Map;

public class TestColorScheme extends ColorScheme {
	
	public static final int JAVA_KEYWORD_ID = 501;
	public static final int JAVA_LINE_COMMENT_ID = 502;
	public static final int JAVA_DOC_COMMENT_ID = 503;
	public static final TextAttribute KEYWORD = new TextAttribute("Java.Keyword", Color.BLUE, null, TextAttribute.BOLD);
	public static final TextAttribute LINE_COMMENT = new TextAttribute("Java.LineComment", Color.GREEN, null, TextAttribute.ITALIC);
	public static final TextAttribute DOC_COMMENT = new TextAttribute("Java.DocComment", Color.GREEN, null, TextAttribute.ITALIC);
	
	public TestColorScheme() {
		super("Test", false);
	}
	
	@Override
	protected void registerDefaults(@NonNull Map<Integer, TextAttribute> attributes) {
		attributes.put(JAVA_KEYWORD_ID, KEYWORD);
		attributes.put(JAVA_LINE_COMMENT_ID, LINE_COMMENT);
		attributes.put(JAVA_DOC_COMMENT_ID, DOC_COMMENT);
		
	}
}
