package com.apkide.common;

public class Location {
	public int x;
	public int y;

	public Location(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean isInvalid() {
		return x >= 0 && y >= 0;
	}
}
