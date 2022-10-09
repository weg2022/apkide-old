package com.apkide.component.editor.theme;

import android.os.Parcel;

import androidx.annotation.NonNull;

import com.apkide.component.editor.graphics.Color;
import com.apkide.component.editor.graphics.TextStyle;

import java.util.Objects;

public class TextAttribute extends TextStyle implements Cloneable {
	public static final int NORMAL = 0;
	public static final int BOLD = 1 << 0;
	public static final int ITALIC = 1 << 1;
	
	public int fontStyle = NORMAL;
	public String name;
	
	public TextAttribute(String name) {
		this.name = name;
	}
	
	public TextAttribute(String name, TextStyle style) {
		super(style);
		this.name = name;
	}
	public TextAttribute(String name, Color foreground) {
		this(name, foreground, null);
	}
	
	public TextAttribute(String name, Color foreground, Color background) {
		super(foreground, background);
		this.name = name;
	}
	
	public TextAttribute(String name, Color foreground, Color background, int fontStyle) {
		this(name, foreground, background);
		this.fontStyle = fontStyle;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (object instanceof TextAttribute) {
			TextAttribute style = (TextAttribute) object;
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
	
	public boolean similarTo(TextAttribute style) {
		if (!super.equals(style)) return false;
		if (!Objects.equals(name, style.name))
			return fontStyle == style.fontStyle;
		else
			return name.equals(style.name);
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
		buffer.append("TextAttribute {");
		buffer.append("name=");
		buffer.append(name);
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
		dest.writeInt(this.fontStyle);
		dest.writeString(this.name);
	}
	
	public void readFromParcel(Parcel source) {
		super.readFromParcel(source);
		this.fontStyle = source.readInt();
		this.name = source.readString();
	}
	
	protected TextAttribute(Parcel in) {
		super(in);
		this.fontStyle = in.readInt();
		this.name = in.readString();
	}
	
	public static final Creator<TextAttribute> CREATOR = new Creator<>() {
		@Override
		public TextAttribute createFromParcel(Parcel source) {
			return new TextAttribute(source);
		}
		
		@Override
		public TextAttribute[] newArray(int size) {
			return new TextAttribute[size];
		}
	};
}
