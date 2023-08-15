package com.apkide.ls.api.callback;

import androidx.annotation.NonNull;

import com.apkide.ls.api.HighlightKind;

public interface HighlighterCallback {
	void highlightStarted(@NonNull String filePath, long version);

	void highlightListEntityFound(@NonNull HighlightKind kind, int startLine, int startColumn, int endLine, int endColumn);

	void highlightCompleted(@NonNull String filePath, long version);

	void semanticHighlightStarted(@NonNull String filePath, long version);

	void semanticHighlightListEntityFound(@NonNull HighlightKind kind, int startLine, int startColumn, int endLine, int endColumn);

	void semanticHighlightCompleted(@NonNull String filePath, long version);
}
