package com.apkide.common;

import java.io.Serializable;
import java.util.Objects;

public class TextStyle implements Serializable {
	private static final long serialVersionUID = -9126858219608783777L;
	public Color backgroundColor;
	public Color fontColor;
	public int fontStyle;

	public TextStyle(Color fontColor) {
		this(fontColor, 0);
	}

	public TextStyle(Color fontColor, int fontStyle) {
		this(null, fontColor, fontStyle);
	}

	public TextStyle(Color backgroundColor, Color fontColor, int fontStyle) {
		this.backgroundColor = backgroundColor;
		this.fontColor = fontColor;
		this.fontStyle = fontStyle;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TextStyle textStyle = (TextStyle) o;

		if (fontStyle != textStyle.fontStyle) return false;
		if (!Objects.equals(backgroundColor, textStyle.backgroundColor))
			return false;
		return Objects.equals(fontColor, textStyle.fontColor);
	}

	@Override
	public int hashCode() {
		int result = backgroundColor != null ? backgroundColor.hashCode() : 0;
		result = 31 * result + (fontColor != null ? fontColor.hashCode() : 0);
		result = 31 * result + fontStyle;
		return result;
	}
}
