package com.apkide.ui;

import static android.content.res.Configuration.UI_MODE_NIGHT_MASK;
import static android.content.res.Configuration.UI_MODE_NIGHT_YES;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;

import com.apkide.language.api.ColorScheme;

import java.io.File;

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
		setColorScheme(colorScheme);
		configure();
	}
	
	public void setDarkTheme(boolean darkTheme) {
		if (colorScheme.isDarkMode() == darkTheme) return;
		colorScheme.setDarkMode(darkTheme);
		setColorScheme(colorScheme);
	}
	
	public boolean isDarkTheme() {
		return colorScheme.isDarkMode();
	}
	
	
	public void configure() {
		boolean dark = false;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			if (getContext().getResources().getConfiguration().isNightModeActive())
				dark = true;
		} else {
			if (isNightModeActive(getResources().getConfiguration().uiMode))
				dark = true;
		}
		setDarkTheme(dark);
		String fontName = AppPreferences.getPreferences().getString("fontTypeface", "JetBrainsMono-Regular.ttf");
		typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts" + File.separator + fontName);
		setTypefaceText(typeface);
		setLigatureEnabled(AppPreferences.getPreferences().getBoolean("ligatureEnabled", true));
		setLineNumberEnabled(AppPreferences.getPreferences().getBoolean("lineNumberShow", true));
		setHighlightCurrentLine(AppPreferences.getPreferences().getBoolean("highlightCaretLine", true));
		setCursorBlinkPeriod(AppPreferences.getPreferences().getBoolean("CaretBlinks", true) ? 500 : -1);
		setTabWidth(AppPreferences.getPreferences().getInt("tabSize", 4));
	}
	
	public boolean isNightModeActive(int uiMode) {
		return (uiMode & UI_MODE_NIGHT_MASK) == UI_MODE_NIGHT_YES;
	}
}
