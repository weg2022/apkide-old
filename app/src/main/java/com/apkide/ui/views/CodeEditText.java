package com.apkide.ui.views;

import static android.view.KeyEvent.KEYCODE_A;
import static android.view.KeyEvent.KEYCODE_BUTTON_A;
import static android.view.KeyEvent.KEYCODE_C;
import static android.view.KeyEvent.KEYCODE_DEL;
import static android.view.KeyEvent.KEYCODE_DPAD_CENTER;
import static android.view.KeyEvent.KEYCODE_DPAD_DOWN;
import static android.view.KeyEvent.KEYCODE_DPAD_LEFT;
import static android.view.KeyEvent.KEYCODE_DPAD_RIGHT;
import static android.view.KeyEvent.KEYCODE_DPAD_UP;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.KeyEvent.KEYCODE_FORWARD_DEL;
import static android.view.KeyEvent.KEYCODE_MOVE_END;
import static android.view.KeyEvent.KEYCODE_MOVE_HOME;
import static android.view.KeyEvent.KEYCODE_PAGE_DOWN;
import static android.view.KeyEvent.KEYCODE_PAGE_UP;
import static android.view.KeyEvent.KEYCODE_TAB;
import static com.apkide.ui.AppCommands.ShortcutKeyCommand;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.annotation.NonNull;

import com.apkide.common.SyntaxKind;
import com.apkide.common.app.AppLog;
import com.apkide.common.keybinding.KeyStroke;
import com.apkide.common.keybinding.KeyStrokeDetector;
import com.apkide.ui.AppCommands;
import com.apkide.ui.views.editor.ActionTypes;
import com.apkide.ui.views.editor.TextStyle;

import java.util.ArrayList;
import java.util.List;

public class CodeEditText extends ViewGroup {
	private final List<ShortcutKeyCommand> myEditorCommands = new ArrayList<>();
	private final TextStyle[] myStyles = new TextStyle[SyntaxKind.values().length];
	private int myDragHandleColor = 0xff0099cc;
	private int myDragHandleDownColor = 0xff004e6a;
	private KeyStrokeDetector.KeyStrokeHandler myStrokeHandler;


	public interface EditorClickedListener {
		void clicked(@NonNull CodeEditText editor, int startLine, int startColumn, int endLine, int endColumn);
	}

	public interface EditorLongPressedListener {
		void longPressed(@NonNull CodeEditText editor, int startLine, int startColumn, int endLine, int endColumn);
	}

	private final List<EditorClickedListener> myClickedListeners = new ArrayList<>(1);
	private final List<EditorLongPressedListener> myLongPressedListeners = new ArrayList<>(1);


	public CodeEditText(Context context) {
		super(context);
		initView();
	}

	public CodeEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}


	public CodeEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	private void initView() {
		removeAllViews();
		addView(new EditorView(getContext()));
		myEditorCommands.add(new EditorActionCommand("Copy",
				new KeyStroke(KEYCODE_C, false, true, false), ActionTypes.CopySelection));
		myEditorCommands.add(new EditorActionCommand("Select All",
				new KeyStroke(KEYCODE_A, false, true, false), ActionTypes.SelectAll));
		myEditorCommands.add(new EditorActionCommand("Delete Character",
				new KeyStroke(KEYCODE_DEL, false, false, false), ActionTypes.RemovePrecedingChar));
		myEditorCommands.add(new EditorActionCommand("Delete Character Right",
				new KeyStroke(KEYCODE_FORWARD_DEL, false, false, false), ActionTypes.RemoveCurrentChar));
		myEditorCommands.add(new EditorActionCommand("Delete Word",
				new KeyStroke(KEYCODE_DEL, false, true, false), ActionTypes.DeleteWordLeft));
		myEditorCommands.add(new EditorActionCommand("Insert Tab",
				new KeyStroke(KEYCODE_TAB, false, false, false), ActionTypes.InsertTab));
		myEditorCommands.add(new EditorActionCommand("Insert LineBreak",
				new KeyStroke(KEYCODE_ENTER, false, false, false), ActionTypes.InsertLineBreak));
		myEditorCommands.add(new EditorActionCommand("Move to Beginning of File",
				new KeyStroke(KEYCODE_DPAD_UP, false, true, true), ActionTypes.MoveCaretToBeginOfText));
		myEditorCommands.add(new EditorActionCommand("Move to End of File",
				new KeyStroke(KEYCODE_DPAD_DOWN, false, true, true), ActionTypes.MoveCaretToEndOfText));
		myEditorCommands.add(new EditorActionCommand("Move to Beginning of Line",
				new KeyStroke(KEYCODE_MOVE_HOME, false, false, false), ActionTypes.MoveCaretToBeginOfTextInLine));
		myEditorCommands.add(new EditorActionCommand("Move to End of Line",
				new KeyStroke(KEYCODE_MOVE_END, false, false, false), ActionTypes.MoveCaretToEndOfLine));
		myEditorCommands.add(new EditorActionCommand("Select to Beginning of File",
				new KeyStroke(KEYCODE_DPAD_UP, true, true, true), ActionTypes.MoveCaretToBeginOfTextSelect));
		myEditorCommands.add(new EditorActionCommand("Select to End of File",
				new KeyStroke(KEYCODE_DPAD_DOWN, true, true, true), ActionTypes.MoveCaretToEndOfTextSelect));
		myEditorCommands.add(new EditorActionCommand("Select to Beginning of Line",
				new KeyStroke(KEYCODE_MOVE_HOME, true, false, false), ActionTypes.MoveCaretToBeginOfTextInLineSelect));
		myEditorCommands.add(new EditorActionCommand("Select to End of Line",
				new KeyStroke(KEYCODE_MOVE_END, true, false, false), ActionTypes.MoveCaretToEndOfLineSelect));
		myEditorCommands.add(new EditorActionCommand("Move Page Up",
				new KeyStroke(KEYCODE_PAGE_UP, false, false, false), ActionTypes.MoveCaretPageUp));
		myEditorCommands.add(new EditorActionCommand("Move Page Down",
				new KeyStroke(KEYCODE_PAGE_DOWN, false, false, false), ActionTypes.MoveCaretPageDown));
		myEditorCommands.add(new EditorActionCommand("Move Up",
				new KeyStroke(KEYCODE_DPAD_UP, false, false, false), ActionTypes.MoveCaretUp));
		myEditorCommands.add(new EditorActionCommand("Move Down",
				new KeyStroke(KEYCODE_DPAD_DOWN, false, false, false), ActionTypes.MoveCaretDown));
		myEditorCommands.add(new EditorActionCommand("Move Left",
				new KeyStroke(KEYCODE_DPAD_LEFT, false, false, false), ActionTypes.MoveCaretLeft));
		myEditorCommands.add(new EditorActionCommand("Move Right",
				new KeyStroke(KEYCODE_DPAD_RIGHT, false, false, false), ActionTypes.MoveCaretRight));
		myEditorCommands.add(new EditorActionCommand("Move Word Left",
				new KeyStroke(KEYCODE_DPAD_LEFT, false, true, false), ActionTypes.MoveCaretWordLeft));
		myEditorCommands.add(new EditorActionCommand("Move Word Right",
				new KeyStroke(KEYCODE_DPAD_RIGHT, false, true, false), ActionTypes.MoveCaretWordRight));
		myEditorCommands.add(new EditorActionCommand("Select Page Up",
				new KeyStroke(KEYCODE_PAGE_UP, true, false, false), ActionTypes.MoveCaretPageUpSelect));
		myEditorCommands.add(new EditorActionCommand("Select Page Down",
				new KeyStroke(KEYCODE_PAGE_DOWN, true, false, false), ActionTypes.MoveCaretPageDownSelect));
		myEditorCommands.add(new EditorActionCommand("Select Word Left",
				new KeyStroke(KEYCODE_DPAD_LEFT, true, true, false), ActionTypes.MoveCaretWordLeftSelect));
		myEditorCommands.add(new EditorActionCommand("Select Word Right",
				new KeyStroke(KEYCODE_DPAD_RIGHT, true, true, false), ActionTypes.MoveCaretWordRightSelect));
		myEditorCommands.add(new EditorActionCommand("Select Up",
				new KeyStroke(KEYCODE_DPAD_UP, true, false, false), ActionTypes.MoveCaretUpSelect));
		myEditorCommands.add(new EditorActionCommand("Select Down",
				new KeyStroke(KEYCODE_DPAD_DOWN, true, false, false), ActionTypes.MoveCaretDownSelect));
		myEditorCommands.add(new EditorActionCommand("Select Left",
				new KeyStroke(KEYCODE_DPAD_LEFT, true, false, false), ActionTypes.MoveCaretLeftSelect));
		myEditorCommands.add(new EditorActionCommand("Select Right",
				new KeyStroke(KEYCODE_DPAD_RIGHT, true, false, false), ActionTypes.MoveCaretRightSelect));

		putTextStyle(SyntaxKind.Plain, new TextStyle(getEditorView().getFontColor()));
		putTextStyle(SyntaxKind.Keyword, new TextStyle(0xff2c82c8, Typeface.BOLD));
		putTextStyle(SyntaxKind.Operator, new TextStyle(0xff007c1f));
		putTextStyle(SyntaxKind.Separator, new TextStyle(0xff0096ff));
		putTextStyle(SyntaxKind.Literal, new TextStyle(0xffbc0000));
		putTextStyle(SyntaxKind.NamespaceIdentifier, new TextStyle(0xff5d5d5d));
		putTextStyle(SyntaxKind.TypeIdentifier, new TextStyle(0xff0096ff));
		putTextStyle(SyntaxKind.VariableIdentifier, new TextStyle(0xff871094));
		putTextStyle(SyntaxKind.FunctionIdentifier, new TextStyle(0xff00627A));
		putTextStyle(SyntaxKind.FunctionCallIdentifier, new TextStyle(0xff00627A, Typeface.BOLD_ITALIC));
		putTextStyle(SyntaxKind.Comment, new TextStyle(0xff009b00, Typeface.ITALIC));
		putTextStyle(SyntaxKind.DocComment, new TextStyle(0xff009b00, Typeface.BOLD_ITALIC));

		myStrokeHandler = new KeyStrokeDetector.KeyStrokeHandler() {

			private AppCommands.ShortcutKeyCommand foundCommand(KeyStroke key) {
				List<AppCommands.ShortcutKeyCommand> commands = getEditorCommands();
				for (AppCommands.ShortcutKeyCommand command : commands) {
					List<KeyStroke> keys = foundKeys(command);
					if (keys != null) {
						for (KeyStroke stroke : keys) {
							if (stroke.matches(key)) {
								return command;
							}
						}
					}
				}
				return null;
			}

			@Override
			public boolean onKeyStroke(KeyStroke keyStroke) {
				AppLog.s("EditorView::onKeyStroke");
				int keyCode = keyStroke.getKeyCode();
				if (keyCode != KEYCODE_BUTTON_A && keyCode != KEYCODE_DPAD_CENTER) {
					AppCommands.ShortcutKeyCommand command = foundCommand(keyStroke);
					if (command != null) {
						if (command.isEnabled()) {
							command.run();
							return true;
						}
					}

					if (keyStroke.isChar()) {
						AppLog.s(String.valueOf(keyStroke.getChar()));
						insertChar(keyStroke.getChar());
						return true;
					}

					if (keyCode == KEYCODE_ENTER) {
						getEditorView().insertLineBreak();
						return true;
					} else {
						return handleKeyStroke(keyStroke);
					}
				}
				return false;
			}
		};
		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	public void putTextStyle(SyntaxKind style, TextStyle textStyle) {
		myStyles[style.ordinal()] = textStyle;
	}

	public void putTextStyle(int style, TextStyle textStyle) {
		myStyles[style] = textStyle;
	}

	public TextStyle getTextStyle(int style) {
		return myStyles[style];
	}


	protected boolean handleKeyStroke(KeyStroke key) {
		return false;
	}

	protected void onKeyStroke() {

	}

	protected List<KeyStroke> foundKeys(ShortcutKeyCommand command) {
		return null;
	}

	public boolean editorViewOnKeyDown(int keyCode, KeyEvent event) {
		return getEditorView().onKeyDown(keyCode, event);
	}

	public boolean editorViewOnKeyUp(int keyCode, KeyEvent event) {
		return getEditorView().onKeyUp(keyCode, event);
	}

	public void insertChar(char c) {
		getEditorView().insertChar(c);
	}


	public void showDragHandle() {
		getScrollView().showDragHandle();
	}


	public void select(int startLine, int startColumn, int endLine, int endColumn) {
		getScrollView().selection(startLine, startColumn, endLine, endColumn);
	}

	public int getSelectionStartLine() {
		if (getEditorView().getSelectionVisibility()) {
			return getEditorView().getFirstSelectedLine();
		}
		return getEditorView().getCaretLine();
	}

	public int getSelectionStartColumn() {
		if (getEditorView().getSelectionVisibility()) {
			return getEditorView().getFirstSelectedColumn();
		}
		return getEditorView().getCaretColumn();
	}

	public int getSelectionEndLine() {
		if (getEditorView().getSelectionVisibility()) {
			return getEditorView().getLastSelectedLine();
		}
		return getEditorView().getCaretLine();
	}

	public int getSelectionEndColumn() {
		if (getEditorView().getSelectionVisibility()) {
			return getEditorView().getLastSelectedColumn();
		}
		return getEditorView().getCaretColumn();
	}

	public int getLineCount() {
		return getEditorView().getEditorModel().getLineCount();
	}

	public int getTabSize() {
		return getEditorView().getTabSize();
	}

	public int getDragHandleColor() {
		return myDragHandleColor;
	}

	public void setDragHandleColor(int dragHandleColor) {
		myDragHandleColor = dragHandleColor;
	}

	public int getDragHandleDownColor() {
		return myDragHandleDownColor;
	}

	public void setDragHandleDownColor(int dragHandleDownColor) {
		myDragHandleDownColor = dragHandleDownColor;
	}

	public void putEditorCommand(ShortcutKeyCommand command) {
		myEditorCommands.remove(command);
		myEditorCommands.add(command);
	}

	public List<ShortcutKeyCommand> getEditorCommands() {
		return myEditorCommands;
	}

	public KeyStrokeDetector getKeyStrokeDetector() {
		return null;
	}


	public EditorView getEditorView() {
		return (EditorView) getChildAt(0);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		getChildAt(0).layout(0, 0, r - l, b - t);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		View view = getChildAt(0);
		view.measure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(view.getMeasuredWidth(), view.getMeasuredHeight());
	}

	public CodeEditTextScrollView getScrollView() {
		return (CodeEditTextScrollView) getParent().getParent();
	}

	public boolean isTouchEventInsideHandle(MotionEvent event) {
		return getScrollView().isTouchEventInsideHandle(event);
	}

	@Override
	public boolean onCheckIsTextEditor() {
		return true;
	}

	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		if (getKeyStrokeDetector() != null) {
			return getKeyStrokeDetector().createInputConnection(this, myStrokeHandler);
		}
		return super.onCreateInputConnection(outAttrs);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (getKeyStrokeDetector() != null) {
			if (getKeyStrokeDetector().onKeyDown(keyCode, event, myStrokeHandler)) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (getKeyStrokeDetector() != null) {
			if (getKeyStrokeDetector().onKeyUp(keyCode, event)) {
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}


	private class EditorActionCommand implements ShortcutKeyCommand {
		private final String name;
		private final KeyStroke key;
		private final ActionTypes action;

		public EditorActionCommand(String name, KeyStroke key, ActionTypes action) {
			this.name = name;
			this.key = key;
			this.action = action;
		}

		@NonNull
		@Override
		public String getName() {
			return name;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		@Override
		public boolean run() {
			getEditorView().getActionRunnable(action).run();
			return true;
		}

		@NonNull
		@Override
		public KeyStroke getKeyStroke() {
			return key;
		}
	}

}
