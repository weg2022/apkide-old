package com.apkide.component.editor.syntax;

import static java.lang.Integer.*;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class SyntaxHighlight implements Comparable<SyntaxHighlight>, Parcelable {
	public int id;
	public int startLine;
	public int startColumn;
	public int endLine;
	public int endColumn;
	
	public SyntaxHighlight(int id, int startLine, int startColumn, int endLine, int endColumn) {
		this.id = id;
		this.startLine = startLine;
		this.startColumn = startColumn;
		this.endLine = endLine;
		this.endColumn = endColumn;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SyntaxHighlight that = (SyntaxHighlight) o;
		return id == that.id && startLine == that.startLine && startColumn == that.startColumn && endLine == that.endLine && endColumn == that.endColumn;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, startLine, startColumn, endLine, endColumn);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeInt(this.startLine);
		dest.writeInt(this.startColumn);
		dest.writeInt(this.endLine);
		dest.writeInt(this.endColumn);
	}
	
	public void readFromParcel(Parcel source) {
		this.id = source.readInt();
		this.startLine = source.readInt();
		this.startColumn = source.readInt();
		this.endLine = source.readInt();
		this.endColumn = source.readInt();
	}
	
	protected SyntaxHighlight(Parcel in) {
		this.id = in.readInt();
		this.startLine = in.readInt();
		this.startColumn = in.readInt();
		this.endLine = in.readInt();
		this.endColumn = in.readInt();
	}
	
	public static final Parcelable.Creator<SyntaxHighlight> CREATOR = new Parcelable.Creator<SyntaxHighlight>() {
		@Override
		public SyntaxHighlight createFromParcel(Parcel source) {
			return new SyntaxHighlight(source);
		}
		
		@Override
		public SyntaxHighlight[] newArray(int size) {
			return new SyntaxHighlight[size];
		}
	};
	
	@Override
	public int compareTo(SyntaxHighlight o) {
		if (startLine == o.startLine) {
			return compare(startColumn, o.startColumn);
		}
		return Integer.compare(startLine, o.startLine);
	}
}
