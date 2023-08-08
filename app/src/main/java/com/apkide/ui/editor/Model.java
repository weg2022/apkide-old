package com.apkide.ui.editor;

import androidx.annotation.NonNull;

import com.apkide.common.Color;
import com.apkide.common.TextStyle;

import java.io.Reader;

public interface Model {

	void addModelListener(@NonNull ModelListener listener);

	void removeModelListener(@NonNull ModelListener listener);

	void insertLineBreak(int line, int column);

	void insert(int line, int column, @NonNull CharSequence text);

	void removeLineBreak(int line);

	void remove(int line, int column);

	void remove(int startLine, int startColumn, int endLine, int endColumn);

	void replace(int startLine, int startColumn, int endLine, int endColumn, @NonNull CharSequence text);

	char getChar(int line, int column);

	@NonNull
	String getText();

	@NonNull
	String getText(int startLine, int startColumn, int endLine, int endColumn);

	@NonNull
	Reader getReader();

	@NonNull
	Reader getReader(int startLine, int startColumn, int endLine, int endColumn);

	int getLineCount();

	int getLineLength(int line);

	int getFullLineLength(int line);

	@NonNull
	String getLineText(int line);

	@NonNull
	String getFullLineText(int line);

	@NonNull
	String getLineBreak(int line);

	@NonNull
	String getLineBreak();

	void setReadOnly(boolean readOnly);

	boolean isReadOnly();

	long getEditTimestamps();

	void beginBatchEdit();

	void endBatchEdit();

	boolean isBatchEdit();

	boolean canUndo();

	boolean canRedo();

	void undo();

	void redo();

	int getStyle(int line, int column);

	TextStyle getDefaultTextStyle(int style);

	boolean isColor(int line, int column);

	Color getColor(int line, int column);

	boolean isWarning(int line, int column);

	boolean isError(int line, int column);

	boolean isDeprecated(int line, int column);
}
