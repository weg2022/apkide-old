package com.apkide.openapi.ls.util;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Position implements Serializable, Comparable<Position> {
	private static final long serialVersionUID = 3906720109778563308L;

	public static final Position UNKNOWN = new Position(-1, -1);
	public int line;
	public int column;

	public Position(int line, int column) {
		this.line = line;
		this.column = column;
	}

	public boolean isInvalid() {
		return line >= 0 && column >= 0;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Position position = (Position) o;

		if (line != position.line) return false;
		return column == position.column;
	}

	@Override
	public int hashCode() {
		int result = line;
		result = 31 * result + column;
		return result;
	}

	@Override
	public int compareTo(Position o) {
		if (line == o.line) {
			return Integer.compare(column, o.column);
		}
		return Integer.compare(line, o.line);
	}

	@NonNull
	@Override
	public String toString() {
		return "(" + line + "," + column + ")";
	}
}
