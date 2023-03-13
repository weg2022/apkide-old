package com.apkide.language.api;

import android.graphics.Color;

import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class ColorScheme extends EditorColorScheme {
	private boolean darkMode=false;
	
	public void setDarkMode(boolean darkMode) {
		if (this.darkMode != darkMode) {
			this.darkMode = darkMode;
			applyDefault();
		}
	}
	
	public boolean isDarkMode() {
		return darkMode;
	}
	
	@Override
	public void applyDefault() {
		super.applyDefault();
		setColor(WHOLE_BACKGROUND, darkMode ? 0xff000000 : 0xffffffff);
		setColor(TEXT_NORMAL, darkMode ? 0xffeeeeee : 0xff000000);
		setColor(CURRENT_LINE, darkMode ? 0xff111c22 : 0xfff5f5f5);
		setColor(LINE_NUMBER, darkMode ? 0xff6ab0e2 : 0xffd0d0d0);
		setColor(LINE_NUMBER_BACKGROUND, Color.TRANSPARENT);
		setColor(LINE_DIVIDER, Color.TRANSPARENT);
		setColor(SELECTED_TEXT_BACKGROUND, 0xff828282);
		setColor(TEXT_SELECTED, 0xffffffff);
		setColor(SELECTION_HANDLE, 0xff0099cc);
		setColor(SELECTION_INSERT, darkMode ? 0xffffffff : 0xff000000);
		setColor(LINE_NUMBER_CURRENT, darkMode ? 0xff6ab0e2 : 0xffd0d0d0);
		
		setColor(SyntaxKind.Plain.ordinal() + 100, darkMode ? 0xffeeeeee : 0xff000000);
		setColor(SyntaxKind.Keyword.ordinal() + 100, darkMode ? 0xff6ab0e2 : 0xff2c82c8);
		setColor(SyntaxKind.Operator.ordinal() + 100, darkMode ? 0xff8ab0e2 : 0xff007c1f);
		setColor(SyntaxKind.Separator.ordinal() + 100, darkMode ? 0xff8ab0e2 : 0xff0096ff);
		setColor(SyntaxKind.Preprocessor.ordinal() + 100, darkMode ? 0xffff8e8e : 0xffbc0000);
		setColor(SyntaxKind.Literal.ordinal() + 100, darkMode ? 0xffff8e8e : 0xffbc0000);
		setColor(SyntaxKind.Identifier.ordinal() + 100, darkMode ? 0xffeeeeee : 0xff000000);
		setColor(SyntaxKind.TypeIdentifier.ordinal() + 100, darkMode ? 0xff99ccee : 0xff0096ff);
		setColor(SyntaxKind.NamespaceIdentifier.ordinal() + 100, darkMode ? 0xffaaaaaa : 0xff5d5d5d);
		setColor(SyntaxKind.DelegateIdentifier.ordinal() + 100, darkMode ? 0xffeeeeee : 0xff000000);
		setColor(SyntaxKind.Comment.ordinal() + 100, darkMode ? 0xff50bb50 : 0xff009b00);
		setColor(SyntaxKind.DocComment.ordinal() + 100, darkMode ? 0xff50bb50 : 0xff009b00);
	}
}
