package com.apkide.ui.views.editor;


import android.os.Parcel;

import androidx.annotation.NonNull;

import com.apkide.ui.views.editor.graphics.Color;
import com.apkide.ui.views.editor.graphics.TextStyle;

public class StyleRange extends TextStyle implements Cloneable {
	public static final int NORMAL = 0;
	public static final int BOLD = 1 << 0;
	public static final int ITALIC = 1 << 1;
	public static final Creator<StyleRange> CREATOR = new Creator<>() {
		@Override
		public StyleRange createFromParcel(Parcel source) {
			return new StyleRange(source);
		}
		
		@Override
		public StyleRange[] newArray(int size) {
			return new StyleRange[size];
		}
	};
	public int start;
	public int length;
	public int fontStyle = NORMAL;
	
	public StyleRange() {
	}
	
	public StyleRange(TextStyle style) {
		super(style);
	}
	
	public StyleRange(int start, int length, Color foreground, Color background) {
		super(foreground, background);
		this.start = start;
		this.length = length;
	}
	
	public StyleRange(int start, int length, Color foreground, Color background, int fontStyle) {
		this(start, length, foreground, background);
		this.fontStyle = fontStyle;
	}
	
	protected StyleRange(Parcel in) {
		super(in);
		this.start = in.readInt();
		this.length = in.readInt();
		this.fontStyle = in.readInt();
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (object instanceof StyleRange) {
			StyleRange style = (StyleRange) object;
			if (start != style.start) return false;
			if (length != style.length) return false;
			return similarTo(style);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() ^ fontStyle;
	}
	
	public boolean isUnStyled() {
		if (foreground != null) return false;
		if (background != null) return false;
		if (fontStyle != NORMAL) return false;
		if (underline) return false;
		if (strikeout) return false;
		return borderStyle == NONE;
	}
	
	public boolean similarTo(StyleRange style) {
		if (!super.equals(style)) return false;
		return fontStyle == style.fontStyle;
	}
	
	@NonNull
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	
	@NonNull
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("StyleRange {");
		buffer.append(start);
		buffer.append(", ");
		buffer.append(length);
		buffer.append(", fontStyle=");
		switch (fontStyle) {
			case BOLD:
				buffer.append("bold");
				break;
			case ITALIC:
				buffer.append("italic");
				break;
			case BOLD | ITALIC:
				buffer.append("bold-italic");
				break;
			default:
				buffer.append("normal");
		}
		String str = super.toString();
		int index = str.indexOf('{');
		str = str.substring(index + 1);
		if (str.length() > 1) buffer.append(", ");
		buffer.append(str);
		return buffer.toString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(this.start);
		dest.writeInt(this.length);
		dest.writeInt(this.fontStyle);
	}
	
	public void readFromParcel(Parcel source) {
		super.readFromParcel(source);
		this.start = source.readInt();
		this.length = source.readInt();
		this.fontStyle = source.readInt();
	}
}
