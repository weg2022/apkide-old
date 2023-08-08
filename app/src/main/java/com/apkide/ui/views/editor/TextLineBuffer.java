package com.apkide.ui.views.editor;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.SpannableStringBuilder;

import androidx.annotation.NonNull;

public class TextLineBuffer implements TextLine {
	private static final int LIMIT = 2048;

	private final SpannableStringBuilder myBuilder = new SpannableStringBuilder();

	public TextLineBuffer(char[] chars) {
		this(String.valueOf(chars));
	}

	public TextLineBuffer(CharSequence text) {
		myBuilder.clear();
		myBuilder.append(text);
	}

	@Override
	public void set(@NonNull char[] chars) {
		set(String.valueOf(chars));
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
	public void append(@NonNull char[] chars) {
		append(String.valueOf(chars));
	}

	@Override
	public void insert(int index, @NonNull CharSequence text) {
		myBuilder.insert(index, text);
	}

	@Override
	public void insert(int index, @NonNull char[] chars) {
		insert(index, String.valueOf(chars));
	}

	@Override
	public void delete(int start, int end) {

		myBuilder.delete(start, end);
	}

	@NonNull
	@Override
	public char[] getArray() {
		return myBuilder.toString().toCharArray();
	}

	@NonNull
	@Override
	public String get() {
		return myBuilder.toString();
	}

	@NonNull
	@Override
	public String get(int start, int end) {
		return myBuilder.subSequence(start, end).toString();
	}

	@Override
	public float measure(int start, int end, @NonNull Paint paint) {
		//  return myBuilder.measureText(start,end,paint);
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
	public void getWidths(int start, int end, @NonNull float[] widths, @NonNull Paint paint) {
		// return myBuilder.getTextWidths(start,end,widths,paint);
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
	public void draw(@NonNull Canvas canvas, int start, int end, float x, float y, @NonNull Paint paint) {
		// myBuilder.drawText(canvas,start,end,x,y,paint);
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
	public void getChars(int start, int end, char[] dest, int destoff) {
		myBuilder.getChars(start, end, dest, destoff);
	}

	@Override
	public int length() {
		return myBuilder.length();
	}

	@Override
	public char charAt(int index) {
		return myBuilder.charAt(index);
	}

	@NonNull
	@Override
	public CharSequence subSequence(int start, int end) {
		return myBuilder.subSequence(start, end);
	}
}
