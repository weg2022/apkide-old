package com.apkide.common;

public class Position {
	public int line;
	public int column;

	public Position(int line, int column) {
		this.line = line;
		this.column = column;
	}

	public boolean isInvalid() {
		return line >= 0 && column >= 0;
	}
}
