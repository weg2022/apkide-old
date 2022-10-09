package com.apkide.component.editor.graphics;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public final class RGB implements Parcelable {
	
	public static final Parcelable.Creator<RGB> CREATOR = new Parcelable.Creator<>() {
		@Override
		public RGB createFromParcel(Parcel source) {
			return new RGB(source);
		}
		
		@Override
		public RGB[] newArray(int size) {
			return new RGB[size];
		}
	};
	public int red;
	public int green;
	public int blue;
	
	
	public RGB(int red, int green, int blue) {
		if ((red > 255) || (red < 0) ||
				(green > 255) || (green < 0) ||
				(blue > 255) || (blue < 0))
			throw new IllegalArgumentException();
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	
	public RGB(float hue, float saturation, float brightness) {
		if (hue < 0 || hue > 360 || saturation < 0 || saturation > 1 ||
				brightness < 0 || brightness > 1) {
			throw new IllegalArgumentException();
		}
		float r, g, b;
		if (saturation == 0) {
			r = g = b = brightness;
		} else {
			if (hue == 360) hue = 0;
			hue /= 60;
			int i = (int) hue;
			float f = hue - i;
			float p = brightness * (1 - saturation);
			float q = brightness * (1 - saturation * f);
			float t = brightness * (1 - saturation * (1 - f));
			switch (i) {
				case 0:
					r = brightness;
					g = t;
					b = p;
					break;
				case 1:
					r = q;
					g = brightness;
					b = p;
					break;
				case 2:
					r = p;
					g = brightness;
					b = t;
					break;
				case 3:
					r = p;
					g = q;
					b = brightness;
					break;
				case 4:
					r = t;
					g = p;
					b = brightness;
					break;
				case 5:
				default:
					r = brightness;
					g = p;
					b = q;
					break;
			}
		}
		red = (int) (r * 255 + 0.5);
		green = (int) (g * 255 + 0.5);
		blue = (int) (b * 255 + 0.5);
	}
	
	
	private RGB(Parcel in) {
		this.red = in.readInt();
		this.green = in.readInt();
		this.blue = in.readInt();
	}
	
	public float[] getHSB() {
		float r = red / 255f;
		float g = green / 255f;
		float b = blue / 255f;
		float max = Math.max(Math.max(r, g), b);
		float min = Math.min(Math.min(r, g), b);
		float delta = max - min;
		float hue = 0;
		float saturation = max == 0 ? 0 : (max - min) / max;
		if (delta != 0) {
			if (r == max) {
				hue = (g - b) / delta;
			} else {
				if (g == max) {
					hue = 2 + (b - r) / delta;
				} else {
					hue = 4 + (r - g) / delta;
				}
			}
			hue *= 60;
			if (hue < 0) hue += 360;
		}
		return new float[]{hue, saturation, max};
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof RGB)) return false;
		RGB rgb = (RGB) object;
		return (rgb.red == this.red) && (rgb.green == this.green) && (rgb.blue == this.blue);
	}
	
	@Override
	public int hashCode() {
		return (blue << 16) | (green << 8) | red;
	}
	
	@NonNull
	@Override
	public String toString() {
		return "RGB {" + red + ", " + green + ", " + blue + "}";
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.red);
		dest.writeInt(this.green);
		dest.writeInt(this.blue);
	}
	
	public void readFromParcel(Parcel source) {
		this.red = source.readInt();
		this.green = source.readInt();
		this.blue = source.readInt();
	}
}
