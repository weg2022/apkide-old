package com.apkide.openapi.ls.callback;

import androidx.annotation.NonNull;

import com.apkide.openapi.ls.HighlightKind;

public interface HighlighterCallback {
	void highlightStarted(@NonNull String filePath, long version);

	void highlightListEntityFound(@NonNull HighlightKind kind, int startLine, int startColumn, int endLine, int endColumn);

	void highlightCompleted(@NonNull String filePath, long version);

	void semanticHighlightStarted(@NonNull String filePath, long version);

	void semanticHighlightListEntityFound(@NonNull HighlightKind kind, int startLine, int startColumn, int endLine, int endColumn);

	void semanticHighlightCompleted(@NonNull String filePath, long version);
}
