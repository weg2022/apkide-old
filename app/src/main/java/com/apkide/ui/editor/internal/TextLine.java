package com.apkide.ui.editor.internal;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.GetChars;

import androidx.annotation.NonNull;

public interface TextLine extends GetChars, CharSequence {
	void set(@NonNull CharSequence text);

	void append(@NonNull CharSequence text);

	void insert(int index, @NonNull CharSequence text);

	void remove(int index);

	void remove(int start, int end);

	void setDelimiter(@NonNull String delimiter);

	@NonNull
	String getDelimiter();

	@Override
	char charAt(int index);

	@Override
	int length();

	int getFullLength();

	@NonNull
	@Override
	CharSequence subSequence(int start, int end);

	@NonNull
	String getText();

	@NonNull
	String getFullText();

	@NonNull
	@Override
	String toString();

	void drawText(@NonNull Canvas canvas, int start, int end, float x, float y,@NonNull Paint paint);

	float measureText(int start, int end,@NonNull Paint paint);

	void getTextWidths(int start, int end,@NonNull float[] widths,@NonNull Paint paint);

}
