package com.apkide.ui.editor.internal;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.SpannableStringBuilder;

import androidx.annotation.NonNull;


public class TextLineBuffer implements TextLine {
	private static final int LIMIT = 32;
	private final SpannableStringBuilder myBuilder = new SpannableStringBuilder();
	private String myDelimiter;

	public TextLineBuffer(){
		this("","");
	}

	public TextLineBuffer(@NonNull CharSequence text, @NonNull String delimiter) {
		myBuilder.append(text);
		myDelimiter = delimiter;
	}

	@Override
	public void set(@NonNull CharSequence text) {
		myBuilder.clear();
		myBuilder.append(text);
	}

	@Override
	public void append(@NonNull CharSequence text) {
		myBuilder.append(text);
	}

	@Override
	public void insert(int index, @NonNull CharSequence text) {
		myBuilder.insert(index, text);
	}

	@Override
	public void remove(int index) {
		myBuilder.delete(index, index + 1);
	}

	@Override
	public void remove(int start, int end) {
		myBuilder.delete(start, end);
	}

	@Override
	public void setDelimiter(@NonNull String delimiter) {
		myDelimiter = delimiter;
	}

	@NonNull
	@Override
	public String getDelimiter() {
		return myDelimiter;
	}

	@Override
	public char charAt(int index) {
		if (index < myBuilder.length()) {
			return myBuilder.charAt(index);
		}
		return myDelimiter.charAt(index - myBuilder.length());
	}

	@Override
	public int length() {
		return myBuilder.length();
	}

	@Override
	public int getFullLength() {
		return myBuilder.length() + myDelimiter.length();
	}

	@NonNull
	@Override
	public CharSequence subSequence(int start, int end) {
		return myBuilder.subSequence(start, end);
	}

	@NonNull
	@Override
	public String getText() {
		return myBuilder.toString();
	}

	@NonNull
	@Override
	public String getFullText() {
		return myBuilder.toString() + myDelimiter;
	}

	@Override
	public void drawText(@NonNull Canvas canvas, int start, int end, float x, float y, @NonNull Paint paint) {
		if (length() < LIMIT || Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			canvas.drawText(myBuilder, start, end, x, y, paint);
			return;
		}
		char[] temp = Buffers.chars(end - start);
		getChars(start, end, temp, 0);
		canvas.drawText(temp, 0, end - start, x, y, paint);
		Buffers.recycle(temp);
	}

	@Override
	public float measureText(int start, int end, @NonNull Paint paint) {
		if (length() < LIMIT || Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			return paint.measureText(myBuilder, start, end);
		}
		char[] temp = Buffers.chars(end - start);
		getChars(start, end, temp, 0);
		float w = paint.measureText(temp, 0, end - start);
		Buffers.recycle(temp);
		return w;
	}

	@Override
	public void getTextWidths(int start, int end, @NonNull float[] widths, @NonNull Paint paint) {
		if (length() < LIMIT || Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			paint.getTextWidths(myBuilder, start, end, widths);
			return;
		}
		char[] temp = Buffers.chars(end - start);
		getChars(start, end, temp, 0);
		paint.getTextWidths(temp, 0, end - start, widths);
		Buffers.recycle(temp);
	}

	@Override
	public void getChars(int start, int end, char[] dest, int destoff) {
		myBuilder.getChars(start, end, dest, destoff);
	}
}
