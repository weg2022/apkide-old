package com.apkide.common;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Color implements Serializable {

	private static final long serialVersionUID = -8789388831000526893L;

	public final int value;

	public Color(@NonNull String hexColor){
		if (hexColor.charAt(0) == '#') {
			long color = Long.parseLong(hexColor.substring(1), 16);
			if (hexColor.length() == 7) {
				color |= 0xffff_ffff_ff00_0000L;
			} else if (hexColor.length() != 9) {
				throw new IllegalArgumentException("Unknown color");
			}
			this.value= (int) color;
		}
		throw new IllegalArgumentException("Unknown color");

	}
	public Color(int a, int r, int g, int b){
		this((a << 24) | (r << 16) | (g << 8) | b);
	}

	public Color(int r, int g, int b){
		this(0xff000000 | (r << 16) | (g << 8) | b);
	}

	public Color(int value) {
		this.value = value;
	}

	public int alpha(){
		return value >>> 24;
	}

	public int red(){
		return (value >> 16) & 0xFF;
	}

	public int green(){
		return (value >> 8) & 0xFF;
	}

	public int blue(){
		return value & 0xFF;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Color color = (Color) o;

		return value == color.value;
	}

	@Override
	public int hashCode() {
		return value;
	}

	@NonNull
	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
