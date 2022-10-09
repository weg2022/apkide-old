package com.apkide.component.editor.graphics;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public final class RGBA implements Parcelable {
	public static final Parcelable.Creator<RGBA> CREATOR = new Parcelable.Creator<>() {
		@Override
		public RGBA createFromParcel(Parcel source) {
			return new RGBA(source);
		}
		
		@Override
		public RGBA[] newArray(int size) {
			return new RGBA[size];
		}
	};
	public RGB rgb;
	public int alpha;
	
	
	public RGBA(int red, int green, int blue, int alpha) {
		if ((alpha > 255) || (alpha < 0)) throw new IllegalArgumentException();
		this.rgb = new RGB(red, green, blue);
		this.alpha = alpha;
	}
	
	
	public RGBA(float hue, float saturation, float brightness, float alpha) {
		if ((alpha > 255) || (alpha < 0)) throw new IllegalArgumentException();
		rgb = new RGB(hue, saturation, brightness);
		this.alpha = (int) (alpha + 0.5);
	}
	
	
	private RGBA(Parcel in) {
		this.rgb = in.readParcelable(RGB.class.getClassLoader());
		this.alpha = in.readInt();
	}
	
	public float[] getHSBA() {
		float[] hsb = rgb.getHSB();
		return new float[]{hsb[0], hsb[1], hsb[2], alpha};
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof RGBA)) return false;
		RGBA rgba = (RGBA) object;
		return (rgba.rgb.red == this.rgb.red) && (rgba.rgb.green == this.rgb.green) && (rgba.rgb.blue == this.rgb.blue)
				&& (rgba.alpha == this.alpha);
	}
	
	@Override
	public int hashCode() {
		return (alpha << 24) | (rgb.blue << 16) | (rgb.green << 8) | rgb.red;
	}
	
	@NonNull
	@Override
	public String toString() {
		return "RGBA {" + rgb.red + ", " + rgb.green + ", " + rgb.blue + ", " + alpha + "}";
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(this.rgb, flags);
		dest.writeInt(this.alpha);
	}
	
	public void readFromParcel(Parcel source) {
		this.rgb = source.readParcelable(RGB.class.getClassLoader());
		this.alpha = source.readInt();
	}
}
