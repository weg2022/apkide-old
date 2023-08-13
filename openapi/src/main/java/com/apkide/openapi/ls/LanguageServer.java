package com.apkide.openapi.ls;

import androidx.annotation.NonNull;

import com.apkide.common.collection.List;
import com.apkide.openapi.ls.util.KeyValue;
import com.apkide.openapi.ls.util.Position;
import com.apkide.openapi.ls.util.Range;

import java.util.Map;

public interface LanguageServer {

	void initialize(int processId, @NonNull String rootPath,
	                @NonNull Map<String, Object> options,
	                @NonNull List<KeyValue<String, String>> workspacePaths);

	void shutdown();

	@NonNull
	String getName();

	@NonNull
	String[] getDefaultFilePatterns();

	void requestHighlighting(@NonNull String filePath);

	void requestCompletion(@NonNull String filePath, @NonNull Position position);

	void gotoDefinition(@NonNull String filePath, @NonNull Position position);

	void findSymbols(@NonNull String filePath);

	void findAPI(@NonNull String filePath, @NonNull Position position);

	void findUsages(@NonNull String filePath, @NonNull Position position, boolean includeDeclaration);

	void prepareRename(@NonNull String filePath, @NonNull Position position, @NonNull String newText);

	void rename(@NonNull String filePath, @NonNull Position position, @NonNull String newText);

	void prepareInlineVariable(@NonNull String filePath, @NonNull Position position);

	void inlineVariable(@NonNull String filePath, @NonNull Position position);

	void prepareInlineMethod(@NonNull String filePath, @NonNull Position position);

	void inlineMethod(@NonNull String filePath, @NonNull Position position);

	void safeDelete(@NonNull String filePath, @NonNull Position position);

	void codeGeneration(@NonNull String filePath, @NonNull Position position);

	void surroundWith(@NonNull String filePath, @NonNull Position position);

	void indent(@NonNull String filePath, int tabSize, int indentationSize);

	void indentLines(@NonNull String filePath, int tabSize, int indentationSize, @NonNull Range range);

	void format(@NonNull String filePath, int tabSize, int indentationSize);

	void formatLines(@NonNull String filePath, int tabSize, int indentationSize, @NonNull Range range);

	void outLineComment(@NonNull String filePath, @NonNull Range range);

	void outDocComment(@NonNull String filePath, @NonNull Range range);

	void unOutComment(@NonNull String filePath, @NonNull Range range);
}
