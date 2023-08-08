package com.apkide.ui.views.editor;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.GetChars;

import androidx.annotation.NonNull;

public interface TextLine extends CharSequence, GetChars {
	void set(@NonNull char[] chars);

	void set(@NonNull CharSequence text);

	void append(@NonNull CharSequence text);

	void append(@NonNull char[] chars);

	void insert(int index, @NonNull CharSequence text);

	void insert(int index, @NonNull char[] chars);

	void delete(int start, int end);

	@NonNull
	char[] getArray();

	@NonNull
	String get();

	@NonNull
	String get(int start, int end);

	float measure(int start, int end, @NonNull Paint paint);

	void getWidths(int start, int end,@NonNull float[] widths, @NonNull Paint paint);

	void draw(@NonNull Canvas canvas, int start, int end, float x, float y, @NonNull Paint paint);
}
