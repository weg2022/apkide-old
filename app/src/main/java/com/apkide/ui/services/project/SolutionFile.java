package com.apkide.ui.services.project;

import java.util.Objects;

public class SolutionFile {
	public String path;
	public String type;
	public boolean binary;

	public SolutionFile(String path, String type, boolean binary) {
		this.path = path;
		this.type = type;
		this.binary = binary;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SolutionFile that = (SolutionFile) o;
		return binary == that.binary && path.equals(that.path) && type.equals(that.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(path, type, binary);
	}
}
