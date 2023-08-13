package com.apkide.openapi.ls.workspace;

import androidx.annotation.NonNull;

import com.apkide.common.collection.List;
import com.apkide.openapi.ls.util.TextEdit;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class WorkspaceEdit implements Serializable {

	private static final long serialVersionUID = -3439117021766444591L;
	@NonNull
	public Map<String, List<TextEdit>> changes;

	public WorkspaceEdit() {
		this(new HashMap<>());
	}

	public WorkspaceEdit(@NonNull Map<String, List<TextEdit>> changes) {
		this.changes = changes;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		WorkspaceEdit that = (WorkspaceEdit) o;

		return changes.equals(that.changes);
	}

	@Override
	public int hashCode() {
		return changes.hashCode();
	}
}
