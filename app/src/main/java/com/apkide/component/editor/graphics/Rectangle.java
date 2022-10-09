package com.apkide.component.editor.graphics;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public final class Rectangle implements Parcelable {
	
	public static final Parcelable.Creator<Rectangle> CREATOR = new Parcelable.Creator<Rectangle>() {
		@Override
		public Rectangle createFromParcel(Parcel source) {
			return new Rectangle(source);
		}
		
		@Override
		public Rectangle[] newArray(int size) {
			return new Rectangle[size];
		}
	};
	public int x;
	public int y;
	public int width;
	public int height;
	
	
	public Rectangle(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	private Rectangle(Parcel in) {
		this.x = in.readInt();
		this.y = in.readInt();
		this.width = in.readInt();
		this.height = in.readInt();
	}
	
	public void add(Rectangle rect) {
		if (rect == null) throw new IllegalArgumentException();
		int left = Math.min(x, rect.x);
		int top = Math.min(y, rect.y);
		int lhs = x + width;
		int rhs = rect.x + rect.width;
		int right = Math.max(lhs, rhs);
		lhs = y + height;
		rhs = rect.y + rect.height;
		int bottom = Math.max(lhs, rhs);
		x = left;
		y = top;
		width = right - left;
		height = bottom - top;
	}
	
	public boolean contains(int x, int y) {
		return (x >= this.x) && (y >= this.y) && x < (this.x + width) && y < (this.y + height);
	}
	
	public boolean contains(Point pt) {
		if (pt == null) throw new IllegalArgumentException();
		return contains(pt.x, pt.y);
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof Rectangle)) return false;
		Rectangle r = (Rectangle) object;
		return (r.x == this.x) && (r.y == this.y) && (r.width == this.width) && (r.height == this.height);
	}
	
	@Override
	public int hashCode() {
		return x ^ y ^ width ^ height;
	}
	
	public void intersect(Rectangle rect) {
		if (rect == null) throw new IllegalArgumentException();
		if (this == rect) return;
		int left = Math.max(x, rect.x);
		int top = Math.max(y, rect.y);
		int lhs = x + width;
		int rhs = rect.x + rect.width;
		int right = Math.min(lhs, rhs);
		lhs = y + height;
		rhs = rect.y + rect.height;
		int bottom = Math.min(lhs, rhs);
		x = right < left ? 0 : left;
		y = bottom < top ? 0 : top;
		width = right < left ? 0 : right - left;
		height = bottom < top ? 0 : bottom - top;
	}
	
	public Rectangle intersection(Rectangle rect) {
		if (rect == null) throw new IllegalArgumentException();
		if (this == rect) return new Rectangle(x, y, width, height);
		int left = Math.max(x, rect.x);
		int top = Math.max(y, rect.y);
		int lhs = x + width;
		int rhs = rect.x + rect.width;
		int right = Math.min(lhs, rhs);
		lhs = y + height;
		rhs = rect.y + rect.height;
		int bottom = Math.min(lhs, rhs);
		return new Rectangle(
				right < left ? 0 : left,
				bottom < top ? 0 : top,
				right < left ? 0 : right - left,
				bottom < top ? 0 : bottom - top);
	}
	
	public boolean intersects(int x, int y, int width, int height) {
		return (x < this.x + this.width) && (y < this.y + this.height) &&
				(x + width > this.x) && (y + height > this.y);
	}
	
	public boolean intersects(Rectangle rect) {
		if (rect == null) throw new IllegalArgumentException();
		return rect == this || intersects(rect.x, rect.y, rect.width, rect.height);
	}
	
	public boolean isEmpty() {
		return (width <= 0) || (height <= 0);
	}
	
	@NonNull
	@Override
	public String toString() {
		return "Rectangle {" + x + ", " + y + ", " + width + ", " + height + "}";
	}
	
	public Rectangle union(Rectangle rect) {
		if (rect == null) throw new IllegalArgumentException();
		int left = Math.min(x, rect.x);
		int top = Math.min(y, rect.y);
		int lhs = x + width;
		int rhs = rect.x + rect.width;
		int right = Math.max(lhs, rhs);
		lhs = y + height;
		rhs = rect.y + rect.height;
		int bottom = Math.max(lhs, rhs);
		return new Rectangle(left, top, right - left, bottom - top);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.x);
		dest.writeInt(this.y);
		dest.writeInt(this.width);
		dest.writeInt(this.height);
	}
	
	public void readFromParcel(Parcel source) {
		this.x = source.readInt();
		this.y = source.readInt();
		this.width = source.readInt();
		this.height = source.readInt();
	}
}
