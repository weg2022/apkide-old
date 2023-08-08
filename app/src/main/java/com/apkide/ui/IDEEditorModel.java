package com.apkide.ui;

import androidx.annotation.NonNull;

import com.apkide.common.FileHighlights;
import com.apkide.common.FileSystem;
import com.apkide.common.app.AppLog;
import com.apkide.common.io.IOUtils;
import com.apkide.ui.services.file.OpenFileService;
import com.apkide.ui.views.CodeEditTextEditorModel;

import java.io.IOException;
import java.io.Reader;

public class IDEEditorModel extends CodeEditTextEditorModel implements OpenFileService.OpenFileModel {

	private final IDEEditor myEditor;
	private final String myFilePath;


	public IDEEditorModel(IDEEditor editor, String filePath) throws IOException {
		super(editor, FileSystem.readFile(filePath));
		myEditor = editor;
		myFilePath = filePath;
	}

	@Override
	public void highlighting(FileHighlights highlights) {
		highlighting(highlights.kinds,
				highlights.startLines,
				highlights.startColumns,
				highlights.endLines,
				highlights.endColumns,
				highlights.size);

	}

	@Override
	public void semanticHighlighting(FileHighlights highlights) {
		semanticHighlighting(highlights.kinds,
				highlights.startLines,
				highlights.startColumns,
				highlights.endLines,
				highlights.endColumns,
				highlights.size);
	}

	@NonNull
	public String getFilePath() {
		return myFilePath;
	}

	@Override
	public long getLastModified() {
		return 0;
	}

	@Override
	public void close() {

	}

	@NonNull
	private String getText(int startLine, int startColumn, int endLine, int endColumn) {
		Reader reader = getReader(startLine, startColumn, endLine, endColumn);
		String text = "";
		try {
			text = IOUtils.readString(reader, true);
		} catch (IOException e) {
			AppLog.e(e);
		}
		return text;
	}

	public int getIndentation(int line) {
		line--;
		if (line < 0) {
			return 0;
		}
		int indent = 0;
		int length = getLineLength(line);
		for (int column = 0; column < length; column++) {
			char c = getChar(line, column);
			if (c == '\t') {
				int tabSize = myEditor.getTabSize();
				indent = ((indent + tabSize) / tabSize) * tabSize;
			} else if (c != ' ') {
				return indent;
			} else {
				indent++;
			}
		}
		return indent;
	}


}
