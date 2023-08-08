package com.apkide.ui.editor.internal;

import androidx.annotation.NonNull;

import com.apkide.common.Color;
import com.apkide.common.TextStyle;
import com.apkide.ui.editor.Model;
import com.apkide.ui.editor.ModelListener;

import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class TextModel implements Model {

	private final List<TextLine> myLines = new LinkedList<>();
	private int mySpanLine;
	private final TextLineBuffer myLineBuffer = new TextLineBuffer();
	private String myLineBreak;
	private final Vector<ModelListener> myListeners = new Vector<>(1);
	private long myEditTimestamps;

	public TextModel() {
		myLineBreak = System.lineSeparator();
		myLines.add(new TextLineImpl());
		myEditTimestamps = -1;
		switchLine(0);
	}

	@Override
	public void addModelListener(@NonNull ModelListener listener) {
		if (!myListeners.contains(listener)) {
			myListeners.add(listener);
		}
	}

	@Override
	public void removeModelListener(@NonNull ModelListener listener) {
		myListeners.remove(listener);
	}

	private void switchLine(int line) {
		if (mySpanLine == line) return;

		if (mySpanLine != -1) {
			myLines.set(mySpanLine, new TextLineImpl(
					myLineBuffer.getText(),
					myLineBuffer.getDelimiter()));
		}

		mySpanLine = line;
		myLineBuffer.set(myLines.get(line).getText());
		myLineBuffer.setDelimiter(myLines.get(line).getDelimiter());
	}

	protected TextLine lineAt(int line) {
		if (line == mySpanLine) {
			return myLineBuffer;
		}
		return myLines.get(line);
	}

	@Override
	public void insertLineBreak(int line, int column) {

	}

	@Override
	public void insert(int line, int column, @NonNull CharSequence text) {
		synchronized (this) {
			switchLine(line);

		}
	}

	@Override
	public void removeLineBreak(int line) {
		synchronized (this) {
			switchLine(line);

		}
	}

	@Override
	public void remove(int line, int column) {
		synchronized (this) {
			switchLine(line);
			myLineBuffer.remove(column);
		}
	}

	@Override
	public void remove(int startLine, int startColumn, int endLine, int endColumn) {
		synchronized (this) {
			if (startLine == endLine) {
				switchLine(startLine);
				myLineBuffer.remove(startColumn, endColumn);
			} else {

			}
		}
	}

	@Override
	public void replace(int startLine, int startColumn, int endLine, int endColumn, @NonNull CharSequence text) {

	}

	@Override
	public char getChar(int line, int column) {
		return lineAt(line).charAt(column);
	}

	@NonNull
	@Override
	public String getText() {
		return getText(0, 0, getLineCount(), 0);
	}

	@NonNull
	@Override
	public String getText(int startLine, int startColumn, int endLine, int endColumn) {
		StringBuilder builder = new StringBuilder();

		if (endLine >= getLineCount()) {
			endLine = getLineCount() - 1;
			endColumn = getFullLineLength(endLine);
		}

		if (endColumn >= getFullLineLength(endLine)) endColumn = getFullLineLength(endLine);

		builder.append(lineAt(startLine).getFullText().substring(startColumn));

		for (int i = startLine + 1; i < endLine; i++) {
			builder.append(lineAt(i).getFullText());
		}
		builder.append(lineAt(endLine).getFullText().substring(0, endColumn));
		return builder.toString();
	}

	@NonNull
	@Override
	public Reader getReader() {
		return getReader(0, 0, getLineCount(), 0);
	}

	@NonNull
	@Override
	public Reader getReader(int startLine, int startColumn, int endLine, int endColumn) {

		return null;
	}

	@Override
	public int getLineCount() {
		return myLines.size();
	}

	@Override
	public int getLineLength(int line) {
		return lineAt(line).length();
	}

	@Override
	public int getFullLineLength(int line) {
		return lineAt(line).getFullLength();
	}

	@NonNull
	@Override
	public String getLineText(int line) {
		return lineAt(line).getText();
	}

	@NonNull
	@Override
	public String getFullLineText(int line) {
		return lineAt(line).getFullText();
	}

	@NonNull
	@Override
	public String getLineBreak(int line) {
		return lineAt(line).getDelimiter();
	}

	@NonNull
	@Override
	public String getLineBreak() {
		return myLineBreak;
	}

	@Override
	public void setReadOnly(boolean readOnly) {

	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public long getEditTimestamps() {
		return myEditTimestamps;
	}

	@Override
	public void beginBatchEdit() {

	}

	@Override
	public void endBatchEdit() {

	}

	@Override
	public boolean isBatchEdit() {
		return false;
	}

	@Override
	public boolean canUndo() {
		return false;
	}

	@Override
	public boolean canRedo() {
		return false;
	}

	@Override
	public void undo() {

	}

	@Override
	public void redo() {

	}

	@Override
	public int getStyle(int line, int column) {
		return 0;
	}

	@Override
	public TextStyle getDefaultTextStyle(int style) {
		return null;
	}

	@Override
	public boolean isColor(int line, int column) {
		return false;
	}

	@Override
	public Color getColor(int line, int column) {
		return null;
	}

	@Override
	public boolean isWarning(int line, int column) {
		return false;
	}

	@Override
	public boolean isError(int line, int column) {
		return false;
	}

	@Override
	public boolean isDeprecated(int line, int column) {
		return false;
	}
}
