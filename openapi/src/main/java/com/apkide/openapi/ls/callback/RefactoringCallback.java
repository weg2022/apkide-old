package com.apkide.openapi.ls.callback;

import androidx.annotation.NonNull;

import com.apkide.openapi.ls.Diagnostic;
import com.apkide.openapi.ls.LogLevel;

public interface RefactoringCallback {

	void reportDiagnostic(@NonNull String filePath, @NonNull Diagnostic diagnostic);

	void reportLog(@NonNull String message, @NonNull LogLevel level);
}
