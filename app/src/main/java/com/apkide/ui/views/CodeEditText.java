package com.apkide.ui.views;

import static android.view.KeyEvent.KEYCODE_A;
import static android.view.KeyEvent.KEYCODE_C;
import static android.view.KeyEvent.KEYCODE_DEL;
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

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.common.KeyStroke;
import com.apkide.common.KeyStrokeDetector;
import com.apkide.common.Styles;
import com.apkide.common.SyntaxKind;
import com.apkide.ui.views.editor.Action;
import com.apkide.ui.views.editor.EditorModel;
import com.apkide.ui.views.editor.Model;
import com.apkide.ui.views.editor.ModelListener;
import com.apkide.ui.views.editor.TextStyle;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CodeEditText extends ViewGroup {
    private final List<KeyStrokeCommand> myEditorCommands = new ArrayList<>();
    private final TextStyle[] myStyles = new TextStyle[SyntaxKind.values().length];
    private int myDragHandleColor=0xff0099cc;
    private int myDragHandleDownColor=0xff004e6a;

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
                new KeyStroke(KEYCODE_C, false, true, false), Action.CopySelection));
        myEditorCommands.add(new EditorActionCommand("Select All",
                new KeyStroke(KEYCODE_A, false, true, false), Action.SelectAll));
        myEditorCommands.add(new EditorActionCommand("Delete Character",
                new KeyStroke(KEYCODE_DEL, false, false, false), Action.RemovePrecedingChar));
        myEditorCommands.add(new EditorActionCommand("Delete Character Right",
                new KeyStroke(KEYCODE_FORWARD_DEL, false, false, false), Action.RemoveCurrentChar));
        myEditorCommands.add(new EditorActionCommand("Delete Word",
                new KeyStroke(KEYCODE_DEL, false, true, false), Action.DeleteWordLeft));
        myEditorCommands.add(new EditorActionCommand("Insert Tab",
                new KeyStroke(KEYCODE_TAB, false, false, false), Action.InsertTab));
        myEditorCommands.add(new EditorActionCommand("Insert LineBreak",
                new KeyStroke(KEYCODE_ENTER, false, false, false), Action.InsertLineBreak));
        myEditorCommands.add(new EditorActionCommand("Move to Beginning of File",
                new KeyStroke(KEYCODE_DPAD_UP, false, true, true), Action.MoveCaretToBeginOfText));
        myEditorCommands.add(new EditorActionCommand("Move to End of File",
                new KeyStroke(KEYCODE_DPAD_DOWN, false, true, true), Action.MoveCaretToEndOfText));
        myEditorCommands.add(new EditorActionCommand("Move to Beginning of Line",
                new KeyStroke(KEYCODE_MOVE_HOME, false, false, false), Action.MoveCaretToBeginOfTextInLine));
        myEditorCommands.add(new EditorActionCommand("Move to End of Line",
                new KeyStroke(KEYCODE_MOVE_END, false, false, false), Action.MoveCaretToEndOfLine));
        myEditorCommands.add(new EditorActionCommand("Select to Beginning of File",
                new KeyStroke(KEYCODE_DPAD_UP, true, true, true), Action.MoveCaretToBeginOfTextSelect));
        myEditorCommands.add(new EditorActionCommand("Select to End of File",
                new KeyStroke(KEYCODE_DPAD_DOWN, true, true, true), Action.MoveCaretToEndOfTextSelect));
        myEditorCommands.add(new EditorActionCommand("Select to Beginning of Line",
                new KeyStroke(KEYCODE_MOVE_HOME, true, false, false), Action.MoveCaretToBeginOfTextInLineSelect));
        myEditorCommands.add(new EditorActionCommand("Select to End of Line",
                new KeyStroke(KEYCODE_MOVE_END, true, false, false), Action.MoveCaretToEndOfLineSelect));
        myEditorCommands.add(new EditorActionCommand("Move Page Up",
                new KeyStroke(KEYCODE_PAGE_UP, false, false, false), Action.MoveCaretPageUp));
        myEditorCommands.add(new EditorActionCommand("Move Page Down",
                new KeyStroke(KEYCODE_PAGE_DOWN, false, false, false), Action.MoveCaretPageDown));
        myEditorCommands.add(new EditorActionCommand("Move Up",
                new KeyStroke(KEYCODE_DPAD_UP, false, false, false), Action.MoveCaretUp));
        myEditorCommands.add(new EditorActionCommand("Move Down",
                new KeyStroke(KEYCODE_DPAD_DOWN, false, false, false), Action.MoveCaretDown));
        myEditorCommands.add(new EditorActionCommand("Move Left",
                new KeyStroke(KEYCODE_DPAD_LEFT, false, false, false), Action.MoveCaretLeft));
        myEditorCommands.add(new EditorActionCommand("Move Right",
                new KeyStroke(KEYCODE_DPAD_RIGHT, false, false, false), Action.MoveCaretRight));
        myEditorCommands.add(new EditorActionCommand("Move Word Left",
                new KeyStroke(KEYCODE_DPAD_LEFT, false, true, false), Action.MoveCaretWordLeft));
        myEditorCommands.add(new EditorActionCommand("Move Word Right",
                new KeyStroke(KEYCODE_DPAD_RIGHT, false, true, false), Action.MoveCaretWordRight));
        myEditorCommands.add(new EditorActionCommand("Select Page Up",
                new KeyStroke(KEYCODE_PAGE_UP, true, false, false), Action.MoveCaretPageUpSelect));
        myEditorCommands.add(new EditorActionCommand("Select Page Down",
                new KeyStroke(KEYCODE_PAGE_DOWN, true, false, false), Action.MoveCaretPageDownSelect));
        myEditorCommands.add(new EditorActionCommand("Select Word Left",
                new KeyStroke(KEYCODE_DPAD_LEFT, true, true, false), Action.MoveCaretWordLeftSelect));
        myEditorCommands.add(new EditorActionCommand("Select Word Right",
                new KeyStroke(KEYCODE_DPAD_RIGHT, true, true, false), Action.MoveCaretWordRightSelect));
        myEditorCommands.add(new EditorActionCommand("Select Up",
                new KeyStroke(KEYCODE_DPAD_UP, true, false, false), Action.MoveCaretUpSelect));
        myEditorCommands.add(new EditorActionCommand("Select Down",
                new KeyStroke(KEYCODE_DPAD_DOWN, true, false, false), Action.MoveCaretDownSelect));
        myEditorCommands.add(new EditorActionCommand("Select Left",
                new KeyStroke(KEYCODE_DPAD_LEFT, true, false, false), Action.MoveCaretLeftSelect));
        myEditorCommands.add(new EditorActionCommand("Select Right",
                new KeyStroke(KEYCODE_DPAD_RIGHT, true, false, false), Action.MoveCaretRightSelect));

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

    protected List<KeyStroke> foundKeys(KeyStrokeCommand command) {
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

    public void focus() {
        if (getEditorView().hasFocus()) {
            return;
        }
        getEditorView().requestFocus();
    }

    public void showDragHandle(){
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

    public void putEditorCommand(KeyStrokeCommand command) {
        myEditorCommands.remove(command);
        myEditorCommands.add(command);
    }

    public List<KeyStrokeCommand> getEditorCommands() {
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

    private class EditorActionCommand implements KeyStrokeCommand {
        private final String name;
        private final KeyStroke key;
        private final Action action;

        public EditorActionCommand(String name, KeyStroke key, Action action) {
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
        public KeyStroke getKey() {
            return key;
        }
    }

    protected class CodeEditTextEditorModel extends EditorModel {
        private final Object myStylesLock = new Object();
        private final Object mySemanticStylesLock = new Object();
        private Styles myStyles = new Styles();
        private Styles myStylesGUI = new Styles();
        private Styles mySemanticsStyles = new Styles();
        private Styles mySemanticsStylesGUI = new Styles();

        public CodeEditTextEditorModel(Reader reader) {
            super(reader, getTabSize());
            init();
        }

        public CodeEditTextEditorModel() {
            super();
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
            return CodeEditText.this.getTextStyle(style);
        }


        public void highlighting(SyntaxKind[] kinds, int[] startLines, int[] startColumns, int[] endLines, int[] endColumns, int size) {
            myStyles.set(kinds, startLines, startColumns, endLines, endColumns, size);
            synchronized (myStylesLock) {
                Styles styles = myStylesGUI;
                myStylesGUI = myStyles;
                myStyles = styles;
            }
            getEditorView().invalidate();
        }

        public void semanticHighlighting(SyntaxKind[] kinds, int[] startLines, int[] startColumns, int[] endLines, int[] endColumns, int size) {
            mySemanticsStyles.set(kinds, startLines, startColumns, endLines, endColumns, size);
            synchronized (mySemanticStylesLock) {
                Styles styles = mySemanticsStylesGUI;
                mySemanticsStylesGUI = mySemanticsStyles;
                mySemanticsStyles = styles;
            }
            getEditorView().invalidate();
        }
    }

}
