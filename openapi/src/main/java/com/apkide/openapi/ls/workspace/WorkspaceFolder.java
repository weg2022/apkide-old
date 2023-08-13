package com.apkide.openapi.ls.workspace;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class WorkspaceFolder implements Serializable {

	private static final long serialVersionUID = 3945453392259963085L;
	@NonNull
	public String filePath;
	@NonNull
	public String name;

	public WorkspaceFolder(@NonNull String filePath,@NonNull String name) {
		this.filePath = filePath;
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		WorkspaceFolder that = (WorkspaceFolder) o;

		if (!filePath.equals(that.filePath)) return false;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		int result = filePath.hashCode();
		result = 31 * result + name.hashCode();
		return result;
	}
}
