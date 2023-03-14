package com.apkide.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.common.AppLog;
import com.apkide.common.IOUtils;
import com.apkide.language.api.ColorScheme;
import com.apkide.ui.services.file.OpenFileModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

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
	
	
	private final ColorScheme colorScheme = new ColorScheme();
	
	private void init() {
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
