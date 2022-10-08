package com.apkide.ui.views.editor.graphics;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public final class Point implements Parcelable {
	
	public static final Parcelable.Creator<Point> CREATOR = new Parcelable.Creator<>() {
		@Override
		public Point createFromParcel(Parcel source) {
			return new Point(source);
		}
		
		@Override
		public Point[] newArray(int size) {
			return new Point[size];
		}
	};
	public int x;
	public int y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	private Point(Parcel in) {
		this.x = in.readInt();
		this.y = in.readInt();
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof Point)) return false;
		Point p = (Point) object;
		return (p.x == this.x) && (p.y == this.y);
	}
	
	@Override
	public int hashCode() {
		return x ^ y;
	}
	
	@NonNull
	@Override
	public String toString() {
		return "Point {" + x + ", " + y + "}";
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.x);
		dest.writeInt(this.y);
	}
	
	public void readFromParcel(Parcel source) {
		this.x = source.readInt();
		this.y = source.readInt();
	}
}

