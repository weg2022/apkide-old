package com.apkide.openapi.ls.util;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class TextEdit implements Serializable {

	private static final long serialVersionUID = 6383730514145065348L;
	@NonNull
	public Range range;
	@NonNull
	public String newText;

	public TextEdit(int line, int column, @NonNull String newText) {
		this(new Range(line, column), newText);
	}

	public TextEdit(@NonNull Range range, @NonNull String newText) {
		this.range = range;
		this.newText = newText;
	}

	public boolean isInvalid(){
		return range.isInvalid();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TextEdit textEdit = (TextEdit) o;

		if (!range.equals(textEdit.range)) return false;
		return newText.equals(textEdit.newText);
	}

	@Override
	public int hashCode() {
		int result = range.hashCode();
		result = 31 * result + newText.hashCode();
		return result;
	}
}
