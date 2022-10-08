package com.apkide.ui.views.editor.graphics;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class TextStyle implements Parcelable {
	public static final int NONE = 0;
	public static final int UNDERLINE_SINGLE = 0;
	
	public static final int UNDERLINE_DOUBLE = 1;
	
	public static final int UNDERLINE_ERROR = 2;
	
	public static final int UNDERLINE_SQUIGGLE = 3;
	
	public static final int UNDERLINE_LINK = 4;
	
	public static final int BORDER_SOLID = 1;
	
	public static final int BORDER_DASH = 2;
	
	public static final int BORDER_DOT = 4;
	public Color foreground;
	public Color background;
	public boolean underline;
	public Color underlineColor;
	public int underlineStyle;
	public boolean strikeout;
	public Color strikeoutColor;
	public int borderStyle;
	public Color borderColor;
	
	public TextStyle() {
	}
	
	
	public TextStyle(Color foreground, Color background) {
		this.foreground = foreground;
		this.background = background;
	}
	
	public TextStyle(TextStyle style) {
		if (style == null) throw new IllegalArgumentException();
		foreground = style.foreground;
		background = style.background;
		underline = style.underline;
		underlineColor = style.underlineColor;
		underlineStyle = style.underlineStyle;
		strikeout = style.strikeout;
		strikeoutColor = style.strikeoutColor;
		borderStyle = style.borderStyle;
		borderColor = style.borderColor;
	}
	
	protected TextStyle(Parcel in) {
		this.foreground = in.readParcelable(Color.class.getClassLoader());
		this.background = in.readParcelable(Color.class.getClassLoader());
		this.underline = in.readByte() != 0;
		this.underlineColor = in.readParcelable(Color.class.getClassLoader());
		this.underlineStyle = in.readInt();
		this.strikeout = in.readByte() != 0;
		this.strikeoutColor = in.readParcelable(Color.class.getClassLoader());
		this.borderStyle = in.readInt();
		this.borderColor = in.readParcelable(Color.class.getClassLoader());
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TextStyle textStyle = (TextStyle) o;
		return underline == textStyle.underline && underlineStyle == textStyle.underlineStyle && strikeout == textStyle.strikeout && borderStyle == textStyle.borderStyle && Objects.equals(foreground, textStyle.foreground) && Objects.equals(background, textStyle.background) && Objects.equals(underlineColor, textStyle.underlineColor) && Objects.equals(strikeoutColor, textStyle.strikeoutColor) && Objects.equals(borderColor, textStyle.borderColor);
	}
	
	@Override
	public int hashCode() {
		int hash = 0;
		if (foreground != null) hash ^= foreground.hashCode();
		if (background != null) hash ^= background.hashCode();
		if (underline) hash ^= (hash << 1);
		if (strikeout) hash ^= (hash << 2);
		if (underlineColor != null) hash ^= underlineColor.hashCode();
		if (strikeoutColor != null) hash ^= strikeoutColor.hashCode();
		if (borderColor != null) hash ^= borderColor.hashCode();
		hash ^= underlineStyle;
		return hash;
	}
	
	boolean isAdherentBorder(TextStyle style) {
		if (this == style) return true;
		if (style == null) return false;
		if (borderStyle != style.borderStyle) return false;
		if (borderColor != null) {
			return borderColor.equals(style.borderColor);
		} else {
			if (style.borderColor != null) return false;
			if (foreground != null) {
				return foreground.equals(style.foreground);
			} else return style.foreground == null;
		}
	}
	
	boolean isAdherentUnderline(TextStyle style) {
		if (this == style) return true;
		if (style == null) return false;
		if (underline != style.underline) return false;
		if (underlineStyle != style.underlineStyle) return false;
		if (underlineColor != null) {
			return underlineColor.equals(style.underlineColor);
		} else {
			if (style.underlineColor != null) return false;
			if (foreground != null) {
				return foreground.equals(style.foreground);
			} else return style.foreground == null;
		}
	}
	
	boolean isAdherentStrikeout(TextStyle style) {
		if (this == style) return true;
		if (style == null) return false;
		if (strikeout != style.strikeout) return false;
		if (strikeoutColor != null) {
			return strikeoutColor.equals(style.strikeoutColor);
		} else {
			if (style.strikeoutColor != null) return false;
			if (foreground != null) {
				return foreground.equals(style.foreground);
			} else return style.foreground == null;
		}
	}
	
	@NonNull
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder("TextStyle {");
		int startLength = buffer.length();
		if (foreground != null) {
			if (buffer.length() > startLength) buffer.append(", ");
			buffer.append("foreground=");
			buffer.append(foreground);
		}
		if (background != null) {
			if (buffer.length() > startLength) buffer.append(", ");
			buffer.append("background=");
			buffer.append(background);
		}
		if (underline) {
			if (buffer.length() > startLength) buffer.append(", ");
			buffer.append("underline=");
			switch (underlineStyle) {
				case UNDERLINE_SINGLE:
					buffer.append("single");
					break;
				case UNDERLINE_DOUBLE:
					buffer.append("double");
					break;
				case UNDERLINE_SQUIGGLE:
					buffer.append("squiggle");
					break;
				case UNDERLINE_ERROR:
					buffer.append("error");
					break;
				case UNDERLINE_LINK:
					buffer.append("link");
					break;
			}
			if (underlineColor != null) {
				buffer.append(", underlineColor=");
				buffer.append(underlineColor);
			}
		}
		if (strikeout) {
			if (buffer.length() > startLength) buffer.append(", ");
			buffer.append("striked out");
			if (strikeoutColor != null) {
				buffer.append(", strikeoutColor=");
				buffer.append(strikeoutColor);
			}
		}
		if (borderStyle != NONE) {
			if (buffer.length() > startLength) buffer.append(", ");
			buffer.append("border=");
			switch (borderStyle) {
				case BORDER_SOLID:
					buffer.append("solid");
					break;
				case BORDER_DOT:
					buffer.append("dot");
					break;
				case BORDER_DASH:
					buffer.append("dash");
					break;
			}
			if (borderColor != null) {
				buffer.append(", borderColor=");
				buffer.append(borderColor);
			}
		}
		buffer.append("}");
		return buffer.toString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(this.foreground, flags);
		dest.writeParcelable(this.background, flags);
		dest.writeByte(this.underline ? (byte) 1 : (byte) 0);
		dest.writeParcelable(this.underlineColor, flags);
		dest.writeInt(this.underlineStyle);
		dest.writeByte(this.strikeout ? (byte) 1 : (byte) 0);
		dest.writeParcelable(this.strikeoutColor, flags);
		dest.writeInt(this.borderStyle);
		dest.writeParcelable(this.borderColor, flags);
	}
	
	public void readFromParcel(Parcel source) {
		this.foreground = source.readParcelable(Color.class.getClassLoader());
		this.background = source.readParcelable(Color.class.getClassLoader());
		this.underline = source.readByte() != 0;
		this.underlineColor = source.readParcelable(Color.class.getClassLoader());
		this.underlineStyle = source.readInt();
		this.strikeout = source.readByte() != 0;
		this.strikeoutColor = source.readParcelable(Color.class.getClassLoader());
		this.borderStyle = source.readInt();
		this.borderColor = source.readParcelable(Color.class.getClassLoader());
	}
}
