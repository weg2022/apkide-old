package com.apkide.ls.api;

import androidx.annotation.NonNull;

import com.apkide.ls.api.util.Position;
import com.apkide.ls.api.util.Range;

import java.util.Map;

public interface LanguageServer {

	void configure(@NonNull Model model);
	
	void initialize(@NonNull String rootPath,
					@NonNull Map<String, Object> options,
	                @NonNull Map<String, String> workspacePaths);

	void shutdown();

	@NonNull
	String getName();

	@NonNull
	String[] getDefaultFilePatterns();

	void requestHighlighting(@NonNull String filePath);

	void requestCompletion(@NonNull String filePath, @NonNull Position position);
	
	void findSymbols(@NonNull String filePath);

	void findAPI(@NonNull String filePath, @NonNull Position position);

	void findUsages(@NonNull String filePath, @NonNull Position position, boolean includeDeclaration);

	void prepareRename(@NonNull String filePath, @NonNull Position position, @NonNull String newName);

	void rename(@NonNull String filePath, @NonNull Position position, @NonNull String newName);

	void prepareInline(@NonNull String filePath, @NonNull Position position);

	void inline(@NonNull String filePath, @NonNull Position position);
	
	void safeDelete(@NonNull String filePath, @NonNull Position position);

	void surroundWith(@NonNull String filePath, @NonNull Position position);

	void indent(@NonNull String filePath, int tabSize, int indentationSize);

	void indent(@NonNull String filePath, int tabSize, int indentationSize, @NonNull Range range);

	void format(@NonNull String filePath, int tabSize, int indentationSize);

	void format(@NonNull String filePath, int tabSize, int indentationSize, @NonNull Range range);

	void comment(@NonNull String filePath, @NonNull Range range);

	void uncomment(@NonNull String filePath, @NonNull Range range);
	
}
