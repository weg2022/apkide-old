package com.apkide.ui.views.editor.syntax;

import static java.lang.Integer.*;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class SyntaxError implements Comparable<SyntaxError>, Parcelable {
	
	public String message;
	public boolean warning;
	public boolean deprecated;
	public int startLine;
	public int startColumn;
	public int endLine;
	public int endColumn;
	
	public SyntaxError(String message, boolean warning, boolean deprecated, int startLine, int startColumn, int endLine, int endColumn) {
		this.message = message;
		this.warning = warning;
		this.deprecated = deprecated;
		this.startLine = startLine;
		this.startColumn = startColumn;
		this.endLine = endLine;
		this.endColumn = endColumn;
	}
	
	@Override
	public int compareTo(SyntaxError o) {
		if (startLine == o.startLine) {
			return compare(startColumn, o.startColumn);
		}
		return compare(startLine, o.startLine);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SyntaxError that = (SyntaxError) o;
		return warning == that.warning && deprecated == that.deprecated && startLine == that.startLine && startColumn == that.startColumn && endLine == that.endLine && endColumn == that.endColumn && message.equals(that.message);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(message, warning, deprecated, startLine, startColumn, endLine, endColumn);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.message);
		dest.writeByte(this.warning ? (byte) 1 : (byte) 0);
		dest.writeByte(this.deprecated ? (byte) 1 : (byte) 0);
		dest.writeInt(this.startLine);
		dest.writeInt(this.startColumn);
		dest.writeInt(this.endLine);
		dest.writeInt(this.endColumn);
	}
	
	public void readFromParcel(Parcel source) {
		this.message = source.readString();
		this.warning = source.readByte() != 0;
		this.deprecated = source.readByte() != 0;
		this.startLine = source.readInt();
		this.startColumn = source.readInt();
		this.endLine = source.readInt();
		this.endColumn = source.readInt();
	}
	
	protected SyntaxError(Parcel in) {
		this.message = in.readString();
		this.warning = in.readByte() != 0;
		this.deprecated = in.readByte() != 0;
		this.startLine = in.readInt();
		this.startColumn = in.readInt();
		this.endLine = in.readInt();
		this.endColumn = in.readInt();
	}
	
	public static final Parcelable.Creator<SyntaxError> CREATOR = new Parcelable.Creator<SyntaxError>() {
		@Override
		public SyntaxError createFromParcel(Parcel source) {
			return new SyntaxError(source);
		}
		
		@Override
		public SyntaxError[] newArray(int size) {
			return new SyntaxError[size];
		}
	};
}
