package com.apkide.common;

import java.io.Serializable;
import java.util.Objects;

public class TextStyle implements Serializable {
	private static final long serialVersionUID = -1244732476231025192L;
	public Color background;
	public Color foreground;
	public int fontStyle;

	public TextStyle(Color foreground) {
		this(foreground,0);
	}

	public TextStyle(Color foreground, int fontStyle) {
		this(null,foreground,fontStyle);
	}

	public TextStyle(Color background, Color foreground) {
		this(background,foreground,0);
	}

	public TextStyle(Color background, Color foreground, int fontStyle) {
		this.background = background;
		this.foreground = foreground;
		this.fontStyle = fontStyle;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TextStyle textStyle = (TextStyle) o;

		if (fontStyle != textStyle.fontStyle) return false;
		if (!Objects.equals(background, textStyle.background))
			return false;
		return Objects.equals(foreground, textStyle.foreground);
	}

	@Override
	public int hashCode() {
		int result = background != null ? background.hashCode() : 0;
		result = 31 * result + (foreground != null ? foreground.hashCode() : 0);
		result = 31 * result + fontStyle;
		return result;
	}
}
