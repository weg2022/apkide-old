package com.apkide.ls.api.callback;

import androidx.annotation.NonNull;

import com.apkide.ls.api.Diagnostic;
import com.apkide.ls.api.LogLevel;

public interface RefactoringCallback {

	void reportDiagnostic(@NonNull String filePath, @NonNull Diagnostic diagnostic);

	void reportLog(@NonNull String message, @NonNull LogLevel level);
}
