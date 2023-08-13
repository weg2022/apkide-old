package com.apkide.openapi.ls.callback;

import androidx.annotation.NonNull;

import com.apkide.common.collection.List;
import com.apkide.openapi.ls.diagnostic.Diagnostic;
import com.apkide.openapi.ls.formatter.FormatterOptions;
import com.apkide.openapi.ls.log.LogLevel;
import com.apkide.openapi.ls.util.Position;
import com.apkide.openapi.ls.util.TextEdit;

import java.util.Map;

public interface RefactoringCallback {

	void reportDiagnostic(@NonNull String filePath, @NonNull Diagnostic diagnostic);

	void reportLog(@NonNull String message, @NonNull LogLevel level);

	void prepareRename(@NonNull String filePath, @NonNull Position position, @NonNull String newName);

	void renameCompleted(@NonNull String filePath, @NonNull Position position, @NonNull String newName, @NonNull Map<String, List<TextEdit>> changes);

	void formatting(@NonNull String filePath, @NonNull FormatterOptions options, @NonNull List<TextEdit> changes);

	void syncWork();

}
