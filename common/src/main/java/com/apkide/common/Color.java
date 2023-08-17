package com.apkide.common;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Color implements Serializable {

	private static final long serialVersionUID = -8789388831000526893L;
	public static final Color White=new Color(255,255,255);
	public static final Color Black=new Color(0,0,0);
	public static final Color Red=new Color(255,0,0);
	public static final Color Lime=new Color(0,255,0);
	public static final Color Blue=new Color(0,0,255);
	public static final Color Yellow=new Color(255,255,0);
	public static final Color Silver=new Color(192,192,192);
	public static final Color Gray=new Color(128,128,128);
	public static final Color Maroon=new Color(128,0,0);
	public static final Color Olive=new Color(128,128,0);
	public static final Color Green=new Color(0,128,0);
	
	public final int value;

	public Color(@NonNull String hexColor){
		if (hexColor.charAt(0) == '#') {
			long color = Long.parseLong(hexColor.substring(1), 16);
			if (hexColor.length() == 7) {
				color |= 0xffffffffff000000L;
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
