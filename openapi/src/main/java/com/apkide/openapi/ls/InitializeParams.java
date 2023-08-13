package com.apkide.openapi.ls;

import androidx.annotation.NonNull;

import com.apkide.common.collection.List;
import com.apkide.openapi.ls.workspace.WorkspaceFolder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class InitializeParams implements Serializable {
	private static final long serialVersionUID = -5067744988448466282L;
	public int processId;
	@NonNull
	public String rootPath;
	@NonNull
	public Map<String,Object> options;
	@NonNull
	public List<WorkspaceFolder> workspaceFolders;

	public InitializeParams(int processId, @NonNull String rootPath) {
		this(processId,rootPath,new HashMap<>(),new List<>());
	}

	public InitializeParams(int processId, @NonNull String rootPath, @NonNull Map<String, Object> options, @NonNull List<WorkspaceFolder> workspaceFolders) {
		this.processId = processId;
		this.rootPath = rootPath;
		this.options = options;
		this.workspaceFolders = workspaceFolders;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		InitializeParams that = (InitializeParams) o;

		if (processId != that.processId) return false;
		if (!rootPath.equals(that.rootPath)) return false;
		if (!options.equals(that.options)) return false;
		return workspaceFolders.equals(that.workspaceFolders);
	}

	@Override
	public int hashCode() {
		int result = processId;
		result = 31 * result + rootPath.hashCode();
		result = 31 * result + options.hashCode();
		result = 31 * result + workspaceFolders.hashCode();
		return result;
	}
}
