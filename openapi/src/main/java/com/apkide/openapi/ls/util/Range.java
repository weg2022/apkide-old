package com.apkide.openapi.ls.util;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class Range implements Serializable, Comparable<Range> {

	private static final long serialVersionUID = 3131892766981125859L;
	@NonNull
	public Position start;
	@NonNull
	public Position end;

	public Range(int line, int column) {
		this(line, column, line, column);
	}

	public Range(int startLine, int startColumn, int endLine, int endColumn) {
		this(new Position(startLine, startColumn), new Position(endLine, endColumn));
	}

	public Range() {
		this(Position.UNKNOWN);
	}

	public Range(@NonNull Position position) {
		this.start = position;
		this.end = position;
	}

	public Range(@NonNull Position start, @NonNull Position end) {
		this.start = start;
		this.end = end;
	}

	public boolean isInvalid() {
		return start.isInvalid() && end.isInvalid();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Range range = (Range) o;

		if (!Objects.equals(start, range.start)) return false;
		return Objects.equals(end, range.end);
	}

	@Override
	public int hashCode() {
		int result = start.hashCode();
		result = 31 * result + end.hashCode();
		return result;
	}

	@Override
	public int compareTo(Range o) {
		if (start.line == o.start.line && start.column == o.start.column) {
			return end.compareTo(o.end);
		}
		return start.compareTo(o.end);
	}

	@NonNull
	@Override
	public String toString() {
		return "(" + start.line + "," + start.column + ":" + end.line + "," + end.column + ")";
	}
}
