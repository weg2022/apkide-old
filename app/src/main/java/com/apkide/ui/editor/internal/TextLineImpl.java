package com.apkide.ui.editor.internal;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.GetChars;

import androidx.annotation.NonNull;

public class TextLineImpl implements TextLine {

	private char[] myChars;
	private String myDelimiter;

	public TextLineImpl(){
		myChars=new char[0];
		myDelimiter="";
	}

	public TextLineImpl(@NonNull CharSequence text, String delimiter) {
		myChars = new char[text.length()];
		if (text instanceof GetChars){
			((GetChars) text).getChars(0,text.length(),myChars,0);
		}else{
			for (int i = 0; i < text.length(); i++) {
				 myChars[i]=text.charAt(i);
			}
		}
		myDelimiter = delimiter;
	}

	@Override
	public void set(@NonNull CharSequence text) {
		myChars = new char[text.length()];
		if (text instanceof GetChars){
			((GetChars) text).getChars(0,text.length(),myChars,0);
		}else{
			for (int i = 0; i < text.length(); i++) {
				myChars[i]=text.charAt(i);
			}
		}
	}

	@Override
	public void append(@NonNull CharSequence text) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void insert(int index, @NonNull CharSequence text) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void remove(int start, int end) {
		throw new UnsupportedOperationException();
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
		if (index < myChars.length) {
			return myChars[index];
		}
		return myDelimiter.charAt(index - myChars.length);
	}

	@Override
	public int length() {
		return myChars.length;
	}

	@Override
	public int getFullLength() {
		return myChars.length + myDelimiter.length();
	}

	@NonNull
	@Override
	public CharSequence subSequence(int start, int end) {
		return String.valueOf(myChars, start, end);
	}

	@NonNull
	@Override
	public String getText() {
		return String.valueOf(myChars);
	}

	@NonNull
	@Override
	public String getFullText() {
		return String.valueOf(myChars) + myDelimiter;
	}

	@NonNull
	@Override
	public String toString() {
		return getFullText();
	}

	@Override
	public void drawText(@NonNull Canvas canvas, int start, int end, float x, float y, @NonNull Paint paint) {
		canvas.drawText(myChars, start, end - start, x, y, paint);
	}

	@Override
	public float measureText(int start, int end, @NonNull Paint paint) {
		return paint.measureText(myChars, start, end - start);
	}

	@Override
	public void getTextWidths(int start, int end, @NonNull float[] widths, @NonNull Paint paint) {
		paint.getTextWidths(myChars, start, end - start, widths);
	}

	@Override
	public void getChars(int start, int end, char[] dest, int destoff) {
		System.arraycopy(myChars, start, dest, destoff, end - start);
	}
}
