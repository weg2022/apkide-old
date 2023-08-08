package com.apkide.ui;

import static com.apkide.ui.AppCommands.ShortcutKeyCommand;

import android.content.Context;
import android.util.AttributeSet;

import com.apkide.common.keybinding.KeyStroke;
import com.apkide.common.keybinding.KeyStrokeDetector;
import com.apkide.ui.services.file.OpenFileService.OpenFileModel;
import com.apkide.ui.views.CodeEditText;

import java.io.IOException;
import java.util.List;

public class IDEEditor extends CodeEditText {
	public IDEEditor(Context context) {
		super(context);
		initView();
	}

	public IDEEditor(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public IDEEditor(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	private void initView() {

	}


	public void makeCaretVisible() {
		getScrollView().scrollToCaretVisible();
	}

	@Override
	protected void onKeyStroke() {
		getScrollView().updateCaret();
	}

	@Override
	protected List<KeyStroke> foundKeys(ShortcutKeyCommand command) {
		return App.getKeyBindingService().getKeys(command);
	}

	@Override
	public KeyStrokeDetector getKeyStrokeDetector() {
		return App.getMainUI().getKeyStrokeDetector();
	}

	public IDEEditorModel getEditorModel() {
		return (IDEEditorModel) getEditorView().getCodeEditTextEditorModel();
	}

	public IDEEditorPager getEditorPager() {
		return App.getMainUI().getEditorPager();
	}

	public int getIndentation(int line) {
		return getEditorModel().getIndentation(line);
	}

	public OpenFileModel openFile(String filePath) {
		OpenFileModel openFileModel = App.getOpenFileService().getFileModel(filePath);
		if (openFileModel == null) {
			openFileModel = createFileModel(filePath);
		}
		getEditorView().setModel((IDEEditorModel) openFileModel);
		getEditorView().setEditable(true);
		requestLayout();
		return openFileModel;
	}

	public OpenFileModel createFileModel(String filePath) {
		try {
			return new IDEEditorModel(this, filePath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


}
