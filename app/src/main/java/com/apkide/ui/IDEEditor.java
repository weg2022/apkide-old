package com.apkide.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.apkide.language.api.ColorScheme;

import io.github.rosemoe.sora.widget.CodeEditor;

public class IDEEditor extends CodeEditor {
	public IDEEditor(Context context) {
		super(context);
		init();
	}
	
	public IDEEditor(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public IDEEditor(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private static Typeface typeface;
	
	private final ColorScheme colorScheme = new ColorScheme();
	
	private void init() {
		if (typeface == null)
			typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/JetBrainsMono-Regular.ttf");
		
		setTypefaceText(typeface);
		setLigatureEnabled(true);
		setColorScheme(colorScheme);
	}
	
	public void setDarkTheme(boolean darkTheme) {
		if (colorScheme.isDarkMode() == darkTheme) return;
		colorScheme.setDarkMode(darkTheme);
		setColorScheme(colorScheme);
	}
	
	public boolean isDarkTheme() {
		return colorScheme.isDarkMode();
	}
	
}
