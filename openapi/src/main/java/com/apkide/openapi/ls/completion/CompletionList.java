package com.apkide.openapi.ls.completion;

import androidx.annotation.NonNull;

import com.apkide.common.collection.List;

import java.io.Serializable;

public class CompletionList implements Serializable {
	public boolean isIncomplete;
	@NonNull
	public List<Completion> entries;

	public CompletionList() {
		this(false,new List<>());
	}

	public CompletionList(boolean isIncomplete, @NonNull List<Completion> entries) {
		this.isIncomplete = isIncomplete;
		this.entries = entries;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CompletionList that = (CompletionList) o;

		if (isIncomplete != that.isIncomplete) return false;
		return entries.equals(that.entries);
	}

	@Override
	public int hashCode() {
		int result = (isIncomplete ? 1 : 0);
		result = 31 * result + entries.hashCode();
		return result;
	}
}
