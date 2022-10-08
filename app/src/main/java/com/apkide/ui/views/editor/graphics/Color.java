package com.apkide.ui.views.editor.graphics;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public final class Color implements Parcelable {
	
	public static final Parcelable.Creator<Color> CREATOR = new Parcelable.Creator<>() {
		@Override
		public Color createFromParcel(Parcel source) {
			return new Color(source);
		}
		
		@Override
		public Color[] newArray(int size) {
			return new Color[size];
		}
	};
	
	public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
	public static final Color WHITE = new Color(255, 255, 255);
	public static final Color BLACK = new Color(0, 0, 0);
	public static final Color NAVY = new Color(0, 0, 128);
	public static final Color BLUE = new Color(0, 0, 255);
	public static final Color GREEN = new Color(0, 128, 0);
	public static final Color TEAL = new Color(0, 128, 128);
	public static final Color LIME = new Color(0, 255, 0);
	public static final Color AQUA = new Color(0, 255, 255);
	public static final Color MAROON = new Color(128, 0, 0);
	public static final Color PURPLE = new Color(128, 0, 128);
	public static final Color OLIVE = new Color(128, 128, 0);
	public static final Color GRAY = new Color(128, 128, 128);
	public static final Color SILVER = new Color(192, 192, 192);
	public static final Color RED = new Color(255, 0, 0);
	public static final Color FUCHSIA = new Color(255, 0, 255);
	public static final Color YELLOW = new Color(255, 255, 0);
	
	
	public double[] handle;
	
	private Color() {
		super();
	}
	
	
	public Color(int red, int green, int blue) {
		super();
		init(red, green, blue, 255);
	}
	
	public Color(int red, int green, int blue, int alpha) {
		super();
		init(red, green, blue, alpha);
	}
	
	
	public Color(RGB rgb) {
		super();
		if (rgb == null) throw new IllegalArgumentException();
		init(rgb.red, rgb.green, rgb.blue, 255);
	}
	
	
	public Color(RGBA rgba) {
		super();
		if (rgba == null) throw new IllegalArgumentException();
		init(rgba.rgb.red, rgba.rgb.green, rgba.rgb.blue, rgba.alpha);
	}
	
	public Color(RGB rgb, int alpha) {
		super();
		if (rgb == null) throw new IllegalArgumentException();
		init(rgb.red, rgb.green, rgb.blue, alpha);
	}
	
	private Color(Parcel in) {
		this.handle = in.createDoubleArray();
	}
	
	
	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof Color)) return false;
		Color color = (Color) object;
		double[] rgbColor = color.handle;
		if (handle == rgbColor) return true;
		return
				(int) (handle[0] * 255) == (int) (rgbColor[0] * 255) &&
						(int) (handle[1] * 255) == (int) (rgbColor[1] * 255) &&
						(int) (handle[2] * 255) == (int) (rgbColor[2] * 255) &&
						(int) (handle[3] * 255) == (int) (rgbColor[3] * 255);
		
	}
	
	public int getAlpha() {
		return (int) (handle[3] * 255);
	}
	
	public int getBlue() {
		return (int) (handle[2] * 255);
	}
	
	public int getGreen() {
		return (int) (handle[1] * 255);
	}
	
	public int getRed() {
		return (int) (handle[0] * 255);
	}
	
	@Override
	public int hashCode() {
		return (int) (handle[0] * 255) ^ (int) (handle[1] * 255) ^ (int) (handle[2] * 255) ^ (int) (handle[3] * 255);
	}
	
	public RGB getRGB() {
		return new RGB(getRed(), getGreen(), getBlue());
	}
	
	public RGBA getRGBA() {
		return new RGBA(getRed(), getGreen(), getBlue(), getAlpha());
	}
	
	void init(int red, int green, int blue, int alpha) {
		if ((red > 255) || (red < 0) ||
				(green > 255) || (green < 0) ||
				(blue > 255) || (blue < 0) ||
				(alpha > 255) || (alpha < 0)) {
			throw new IllegalArgumentException();
		}
		double[] rgbColor = new double[4];
		rgbColor[0] = red / 255f;
		rgbColor[1] = green / 255f;
		rgbColor[2] = blue / 255f;
		rgbColor[3] = alpha / 255f;
		handle = rgbColor;
	}
	
	@NonNull
	@Override
	public String toString() {
		return "Color {" + getRed() + ", " + getGreen() + ", " + getBlue() + ", " + getAlpha() + "}";
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDoubleArray(this.handle);
	}
	
	public void readFromParcel(Parcel source) {
		this.handle = source.createDoubleArray();
	}
}
