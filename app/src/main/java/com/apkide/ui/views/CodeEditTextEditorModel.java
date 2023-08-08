package com.apkide.ui.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.common.Styles;
import com.apkide.common.SyntaxKind;
import com.apkide.ui.views.editor.EditorModel;
import com.apkide.ui.views.editor.Model;
import com.apkide.ui.views.editor.ModelListener;
import com.apkide.ui.views.editor.TextStyle;

import java.io.Reader;

public class CodeEditTextEditorModel extends EditorModel {
	private final CodeEditText myCodeEditText;
	private final Object myStylesLock = new Object();
	private final Object mySemanticStylesLock = new Object();
	private Styles myStyles = new Styles();
	private Styles myStylesGUI = new Styles();
	private Styles mySemanticsStyles = new Styles();
	private Styles mySemanticsStylesGUI = new Styles();

	public CodeEditTextEditorModel(CodeEditText codeEditText, Reader reader) {
		super(reader);
		myCodeEditText = codeEditText;
		init();
	}

	public CodeEditTextEditorModel(CodeEditText codeEditText) {
		super();
		myCodeEditText = codeEditText;
		init();
	}

	private void init() {
		addModelListener(new ModelListener() {
			@Override
			public void insertUpdate(@NonNull Model model, int startLine, int startColumn, int endLine, int endColumn) {
				synchronized (myStylesLock) {
					myStylesGUI.insert(startLine, startColumn, endLine, endColumn);
				}
				synchronized (mySemanticStylesLock) {
					mySemanticsStylesGUI.insert(startLine, startColumn, endLine, endColumn);
				}
			}

			@Override
			public void removeUpdate(@NonNull Model model, int startLine, int startColumn, int endLine, int endColumn) {
				synchronized (myStylesLock) {
					myStylesGUI.remove(startLine, startColumn, endLine, endColumn);
				}
				synchronized (mySemanticStylesLock) {
					mySemanticsStylesGUI.remove(startLine, startColumn, endLine, endColumn);
				}
			}
		});
	}


	@Override
	public int getStyle(int line, int column) {
		int style = mySemanticsStylesGUI.getStyle(line, column);
		if (style == 0) {
			return myStylesGUI.getStyle(line, column);
		}
		return style;
	}

	@Override
	public int getStyleCount() {
		return SyntaxKind.values().length;
	}

	@Nullable
	@Override
	public TextStyle getTextStyle(int style) {
		return myCodeEditText.getTextStyle(style);
	}


	public void highlighting(SyntaxKind[] kinds, int[] startLines, int[] startColumns, int[] endLines, int[] endColumns, int size) {
		myStyles.set(kinds, startLines, startColumns, endLines, endColumns, size);
		synchronized (myStylesLock) {
			Styles styles = myStylesGUI;
			myStylesGUI = myStyles;
			myStyles = styles;
		}
		myCodeEditText.getEditorView().invalidate();
	}

	public void semanticHighlighting(SyntaxKind[] kinds, int[] startLines, int[] startColumns, int[] endLines, int[] endColumns, int size) {
		mySemanticsStyles.set(kinds, startLines, startColumns, endLines, endColumns, size);
		synchronized (mySemanticStylesLock) {
			Styles styles = mySemanticsStylesGUI;
			mySemanticsStylesGUI = mySemanticsStyles;
			mySemanticsStyles = styles;
		}
		myCodeEditText.getEditorView().invalidate();
	}
}
