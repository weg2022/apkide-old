package com.apkide.openapi.ls.util;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Location implements Serializable, Comparable<Location> {
	private static final long serialVersionUID = 1653269106520888834L;
	@NonNull
	public String filePath;
	@NonNull
	public Range range;

	public Location(@NonNull String filePath) {
		this(filePath, new Range());
	}

	public Location(@NonNull String filePath, int line, int column) {
		this(filePath, new Range(line, column));
	}

	public Location(@NonNull String filePath, int startLine, int startColumn, int endLine, int endColumn) {
		this(filePath, new Range(startLine, startColumn, endLine, endColumn));
	}

	public Location(@NonNull String filePath, @NonNull Position start, @NonNull Position end) {
		this(filePath, new Range(start, end));
	}

	public Location(@NonNull String filePath, @NonNull Range range) {
		this.filePath = filePath;
		this.range = range;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Location location = (Location) o;

		if (!filePath.equals(location.filePath)) return false;
		return range.equals(location.range);
	}

	@Override
	public int hashCode() {
		int result = filePath.hashCode();
		result = 31 * result + range.hashCode();
		return result;
	}

	@Override
	public int compareTo(Location o) {
		return range.compareTo(o.range);
	}

	@NonNull
	@Override
	public String toString() {
		return "(" + filePath + ":" +
				range.start.line + "," +
				range.start.column + ":" +
				range.end.line + "," +
				range.end.column + ")";
	}
}
