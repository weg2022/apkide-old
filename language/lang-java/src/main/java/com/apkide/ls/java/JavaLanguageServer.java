package com.apkide.ls.java;

import androidx.annotation.NonNull;

import com.apkide.ls.api.Feature;
import com.apkide.ls.api.LanguageServer;
import com.apkide.ls.api.Model;
import com.apkide.ls.api.util.Position;
import com.apkide.ls.api.util.Range;

import java.util.HashMap;
import java.util.Map;

public class JavaLanguageServer implements LanguageServer {

	private final Model myModel;


	public JavaLanguageServer(Model model) {
		myModel = model;
	}

	@Override
	public void initialize(int processId, @NonNull String rootPath, @NonNull Map<String, Object> options, @NonNull Map<String, String> workspacePaths) {
		Map<String, Object> result = new HashMap<>();
		result.put(Feature.CodeCompletion.name, true);

		myModel.getInitializerCallback().initializeCompleted(result);
	}

	@Override
	public void shutdown() {

	}

	@NonNull
	@Override
	public String getName() {
		return "Java";
	}

	@NonNull
	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{"*.class", "*.java"};
	}

	@Override
	public void requestHighlighting(@NonNull String filePath) {

	}

	@Override
	public void requestCompletion(@NonNull String filePath, @NonNull Position position) {

	}

	@Override
	public void gotoDefinition(@NonNull String filePath, @NonNull Position position) {

	}

	@Override
	public void findSymbols(@NonNull String filePath) {

	}

	@Override
	public void findAPI(@NonNull String filePath, @NonNull Position position) {

	}

	@Override
	public void findUsages(@NonNull String filePath, @NonNull Position position, boolean includeDeclaration) {

	}

	@Override
	public void prepareRename(@NonNull String filePath, @NonNull Position position, @NonNull String newText) {

	}

	@Override
	public void rename(@NonNull String filePath, @NonNull Position position, @NonNull String newText) {

	}

	@Override
	public void prepareInlineVariable(@NonNull String filePath, @NonNull Position position) {

	}

	@Override
	public void inlineVariable(@NonNull String filePath, @NonNull Position position) {

	}

	@Override
	public void prepareInlineMethod(@NonNull String filePath, @NonNull Position position) {

	}

	@Override
	public void inlineMethod(@NonNull String filePath, @NonNull Position position) {

	}

	@Override
	public void safeDelete(@NonNull String filePath, @NonNull Position position) {

	}

	@Override
	public void codeGeneration(@NonNull String filePath, @NonNull Position position) {

	}

	@Override
	public void surroundWith(@NonNull String filePath, @NonNull Position position) {

	}

	@Override
	public void indent(@NonNull String filePath, int tabSize, int indentationSize) {

	}

	@Override
	public void indentLines(@NonNull String filePath, int tabSize, int indentationSize, @NonNull Range range) {

	}

	@Override
	public void format(@NonNull String filePath, int tabSize, int indentationSize) {

	}

	@Override
	public void formatLines(@NonNull String filePath, int tabSize, int indentationSize, @NonNull Range range) {

	}

	@Override
	public void outLineComment(@NonNull String filePath, @NonNull Range range) {

	}

	@Override
	public void outDocComment(@NonNull String filePath, @NonNull Range range) {

	}

	@Override
	public void unOutComment(@NonNull String filePath, @NonNull Range range) {

	}
}
