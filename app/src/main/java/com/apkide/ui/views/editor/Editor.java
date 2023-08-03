package com.apkide.ui.views.editor;

import static com.apkide.ui.views.editor.Action.AutoIndent;
import static com.apkide.ui.views.editor.Action.CommentSelection;
import static com.apkide.ui.views.editor.Action.CopySelection;
import static com.apkide.ui.views.editor.Action.CutSelection;
import static com.apkide.ui.views.editor.Action.DeleteWordLeft;
import static com.apkide.ui.views.editor.Action.DeleteWordRight;
import static com.apkide.ui.views.editor.Action.IndentSelection;
import static com.apkide.ui.views.editor.Action.InsertLineBreak;
import static com.apkide.ui.views.editor.Action.InsertTab;
import static com.apkide.ui.views.editor.Action.MoveCaretDown;
import static com.apkide.ui.views.editor.Action.MoveCaretDownSelect;
import static com.apkide.ui.views.editor.Action.MoveCaretLeft;
import static com.apkide.ui.views.editor.Action.MoveCaretLeftSelect;
import static com.apkide.ui.views.editor.Action.MoveCaretPageDown;
import static com.apkide.ui.views.editor.Action.MoveCaretPageDownSelect;
import static com.apkide.ui.views.editor.Action.MoveCaretPageUp;
import static com.apkide.ui.views.editor.Action.MoveCaretPageUpSelect;
import static com.apkide.ui.views.editor.Action.MoveCaretRight;
import static com.apkide.ui.views.editor.Action.MoveCaretRightSelect;
import static com.apkide.ui.views.editor.Action.MoveCaretToBeginOfLine;
import static com.apkide.ui.views.editor.Action.MoveCaretToBeginOfLineSelect;
import static com.apkide.ui.views.editor.Action.MoveCaretToBeginOfText;
import static com.apkide.ui.views.editor.Action.MoveCaretToBeginOfTextInLine;
import static com.apkide.ui.views.editor.Action.MoveCaretToBeginOfTextInLineSelect;
import static com.apkide.ui.views.editor.Action.MoveCaretToBeginOfTextSelect;
import static com.apkide.ui.views.editor.Action.MoveCaretToEndOfLine;
import static com.apkide.ui.views.editor.Action.MoveCaretToEndOfLineSelect;
import static com.apkide.ui.views.editor.Action.MoveCaretToEndOfText;
import static com.apkide.ui.views.editor.Action.MoveCaretToEndOfTextSelect;
import static com.apkide.ui.views.editor.Action.MoveCaretUp;
import static com.apkide.ui.views.editor.Action.MoveCaretUpSelect;
import static com.apkide.ui.views.editor.Action.MoveCaretWordLeft;
import static com.apkide.ui.views.editor.Action.MoveCaretWordLeftSelect;
import static com.apkide.ui.views.editor.Action.MoveCaretWordRight;
import static com.apkide.ui.views.editor.Action.MoveCaretWordRightSelect;
import static com.apkide.ui.views.editor.Action.PasteSelection;
import static com.apkide.ui.views.editor.Action.Redo;
import static com.apkide.ui.views.editor.Action.RemoveCurrentChar;
import static com.apkide.ui.views.editor.Action.RemoveCurrentLine;
import static com.apkide.ui.views.editor.Action.RemovePrecedingChar;
import static com.apkide.ui.views.editor.Action.RemoveSelection;
import static com.apkide.ui.views.editor.Action.SelectAll;
import static com.apkide.ui.views.editor.Action.ToggleOverwriteMode;
import static com.apkide.ui.views.editor.Action.UnCommentSelection;
import static com.apkide.ui.views.editor.Action.UnIndentSelection;
import static com.apkide.ui.views.editor.Action.Undo;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.apkide.common.IOUtils;

import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;

public class Editor extends Console {
    public Editor(Context context) {
        super(context);
        initView();
    }

    public Editor(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public Editor(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private abstract class UnSelectActionRunnable implements ActionRunnable {
        @Override
        public void run() {
            if (getSelectionVisibility() && !myInSelecting) {
                setSelectionVisibility(false);
            }
        }
    }

    private class SelectActionRunnable implements ActionRunnable {
        private final ActionRunnable myRunnable;

        public SelectActionRunnable(Action action) {
            myRunnable = getActionRunnable(action);
        }

        @Override
        public void run() {
            if (!myInSelecting)
                setSelectionAnchor(getCaretLine(), getCaretColumn());

            myInSelecting = true;
            myRunnable.run();
            myInSelecting = false;
            setSelectionPoint(getCaretLine(), getCaretColumn());
            setSelectionVisibility(true);
        }
    }


    private enum State {
        NONE,
        INSERTING,
        OVERWRITING,
        DELETING,
        BACKSPACING,
        MOVING_VERTICALLY,
    }

    private boolean myEditable;
    private State myInternalState;
    private float myIntendedColumnX;
    private final Hashtable<Action, ActionRunnable> myActions = new Hashtable<>();
    private boolean myInSelecting;

    private void initView() {
        setModel(new EditorModel());//Default
        boundAction(MoveCaretLeft, new UnSelectActionRunnable() {
            @Override
            public void run() {
                super.run();
                setInternalState(State.NONE);
                int caretLine = getCaretLine();
                int caretColumn = getCaretColumn();
                if (caretLine == 0 && caretColumn == 0) {
                    View focus = focusSearch(View.FOCUS_UP);
                    if (focus != null)
                        focus.requestFocus();
                } else if (caretColumn != 0)
                    moveCaret(caretLine, caretColumn - 1);
                else
                    moveCaret(caretLine - 1, getEditorModel().getLineLength(caretLine - 1));
            }
        });

        boundAction(MoveCaretRight, new UnSelectActionRunnable() {
            @Override
            public void run() {
                super.run();
                setInternalState(State.NONE);
                int caretLine = getCaretLine();
                int caretColumn = getCaretColumn();
                if (caretColumn != getEditorModel().getLineLength(caretLine))
                    moveCaret(caretLine, caretColumn + 1);
                else if (caretLine != getEditorModel().getLineCount() - 1)
                    moveCaret(caretLine + 1, 0);
            }
        });

        boundAction(MoveCaretUp, new UnSelectActionRunnable() {
            @Override
            public void run() {
                super.run();
                int caretLine = getCaretLine();
                int caretColumn = getCaretColumn();
                if (caretLine != 0) {
                    setInternalState(State.MOVING_VERTICALLY);

                    setIntendedColumnX(computeLocationX(caretLine, caretColumn));
                    caretLine--;
                    moveCaret(caretLine, computeColumn(caretLine, getIntendedColumnX()));
                    return;
                }
                View view = focusSearch(View.FOCUS_UP);
                if (view != null)
                    view.requestFocus();
            }
        });

        boundAction(MoveCaretDown, new UnSelectActionRunnable() {
            @Override
            public void run() {
                super.run();
                int caretLine = getCaretLine();
                int caretColumn = getCaretColumn();
                setInternalState(State.MOVING_VERTICALLY);
                setIntendedColumnX(computeLocationX(caretLine, caretColumn));
                caretLine++;
                if (caretLine != getEditorModel().getLineCount())
                    moveCaret(caretLine, computeColumn(caretLine, getIntendedColumnX()));
            }
        });
        boundAction(MoveCaretPageUp, new UnSelectActionRunnable() {
            @Override
            public void run() {
                super.run();
                int caretLine = getCaretLine();
                int caretColumn = getCaretColumn();
                setInternalState(State.MOVING_VERTICALLY);
                setIntendedColumnX(computeLocationX(caretLine, caretColumn));
                for (int i = 0; caretLine > 0 && i < 10; i++) {
                    caretLine--;
                }
                moveCaret(caretLine, computeColumn(caretLine, getIntendedColumnX()));
            }
        });

        boundAction(MoveCaretPageDown, new UnSelectActionRunnable() {
            @Override
            public void run() {
                super.run();
                int caretLine = getCaretLine();
                int caretColumn = getCaretColumn();
                setInternalState(State.MOVING_VERTICALLY);
                setIntendedColumnX(computeLocationX(caretLine, caretColumn));
                for (int i = 0; caretLine < getEditorModel().getLineCount() - 1 && i < 10; i++) {
                    caretLine++;
                }
                moveCaret(caretLine, computeColumn(caretLine, getIntendedColumnX()));
            }
        });

        boundAction(MoveCaretToBeginOfLine, new UnSelectActionRunnable() {
            @Override
            public void run() {
                super.run();
                setInternalState(State.NONE);
                moveCaret(getCaretLine(), 0);
            }
        });

        boundAction(MoveCaretToBeginOfTextInLine, new UnSelectActionRunnable() {
            @Override
            public void run() {
                super.run();
                setInternalState(State.NONE);
                int caretLine = getCaretLine();
                int caretColumn = getCaretColumn();

                int column = 0;
                int col = 0;
                char c;
                while (col < getEditorModel().getLineLength(caretLine) &&
                        ((c = getEditorModel().getChar(caretLine, col)) == '\t' || c == ' ')) {
                    col++;
                }

                if (col != caretColumn && col != getEditorModel().getLineLength(caretLine))
                    column = col;

                moveCaret(caretLine, column);
            }
        });

        boundAction(MoveCaretToEndOfLine, new UnSelectActionRunnable() {
            @Override
            public void run() {
                super.run();
                setInternalState(State.NONE);
                moveCaret(getCaretLine(), getEditorModel().getLineLength(getCaretLine()));
            }
        });

        boundAction(MoveCaretToBeginOfText, new UnSelectActionRunnable() {
            @Override
            public void run() {
                super.run();
                setInternalState(State.NONE);
                moveCaret(0, 0);
            }
        });

        boundAction(MoveCaretToEndOfText, new UnSelectActionRunnable() {
            @Override
            public void run() {
                super.run();
                setInternalState(State.NONE);
                int line = getEditorModel().getLineCount() - 1;
                moveCaret(line, getEditorModel().getLineLength(line));
            }
        });

        boundAction(MoveCaretWordLeft, new UnSelectActionRunnable() {
            @Override
            public void run() {
                super.run();
                setInternalState(State.NONE);
                int caretLine = getCaretLine();
                int caretColumn = getCaretColumn();
                if (caretColumn == 0) {
                    if (caretLine == 0) return;
                    caretLine--;
                    caretColumn = getEditorModel().getLineLength(caretLine);
                }
                if (caretColumn != 0 && caretColumn - 1 != 0) {
                    while (caretColumn > 0 && Character.isWhitespace(
                            getEditorModel().getChar(caretLine, caretColumn))) {
                        caretColumn--;
                    }
                    if (caretColumn != 0) {
                        boolean isIdentifier = Character.isJavaIdentifierPart(
                                getEditorModel().getChar(caretLine, caretColumn));
                        while (caretColumn > 0) {
                            char c = getEditorModel().getChar(caretLine, caretColumn - 1);
                            if (isIdentifier != Character.isJavaIdentifierPart(c)
                                    || Character.isWhitespace(c)) {
                                break;
                            }
                            caretColumn--;
                        }
                    }
                }
                moveCaret(caretLine, caretColumn);
            }
        });

        boundAction(MoveCaretWordRight, new UnSelectActionRunnable() {
            @Override
            public void run() {
                super.run();
                setInternalState(State.NONE);
                int caretLine = getCaretLine();
                int caretColumn = getCaretColumn();
                if (caretColumn == getEditorModel().getLineLength(caretLine)) {
                    if (caretLine == getEditorModel().getLineCount() - 1) {
                        return;
                    }
                    int line = caretLine + 1;
                    int col = 0;
                    while (col < getEditorModel().getLineLength(line) &&
                            Character.isWhitespace(getEditorModel().getChar(line, col))) {
                        col++;
                    }
                    moveCaret(line, col);
                    return;
                }
                if (caretColumn != getEditorModel().getLineLength(caretLine)) {
                    boolean isIdentifier = Character.isJavaIdentifierPart(
                            getEditorModel().getChar(caretLine, caretColumn));
                    while (caretColumn < getEditorModel().getLineLength(caretLine)) {
                        char c = getEditorModel().getChar(caretLine, caretColumn);
                        if (Character.isJavaIdentifierPart(c) != isIdentifier
                                || Character.isWhitespace(c)) {
                            break;
                        }
                        caretColumn++;
                    }
                    while (caretColumn < getEditorModel().getLineLength(caretLine)
                            && Character.isWhitespace(
                            getEditorModel().getChar(caretLine, caretColumn))) {
                        caretColumn++;
                    }

                }
                moveCaret(caretLine, caretColumn);
            }
        });

        boundAction(MoveCaretLeftSelect, new SelectActionRunnable(MoveCaretLeft));
        boundAction(MoveCaretRightSelect, new SelectActionRunnable(MoveCaretRight));
        boundAction(MoveCaretUpSelect, new SelectActionRunnable(MoveCaretUp));
        boundAction(MoveCaretDownSelect, new SelectActionRunnable(MoveCaretDown));
        boundAction(MoveCaretPageUpSelect, new SelectActionRunnable(MoveCaretPageUp));
        boundAction(MoveCaretPageDownSelect, new SelectActionRunnable(MoveCaretPageDown));
        boundAction(MoveCaretToBeginOfLineSelect, new SelectActionRunnable(MoveCaretToBeginOfLine));
        boundAction(MoveCaretToBeginOfTextInLineSelect, new SelectActionRunnable(MoveCaretToBeginOfTextInLine));
        boundAction(MoveCaretToEndOfLineSelect, new SelectActionRunnable(MoveCaretToEndOfLine));
        boundAction(MoveCaretToBeginOfTextSelect, new SelectActionRunnable(MoveCaretToBeginOfText));
        boundAction(MoveCaretToEndOfTextSelect, new SelectActionRunnable(MoveCaretToEndOfText));
        boundAction(MoveCaretWordLeftSelect, new SelectActionRunnable(MoveCaretWordLeft));
        boundAction(MoveCaretWordRightSelect, new SelectActionRunnable(MoveCaretWordRight));

        boundAction(DeleteWordLeft, () -> {
            if (isEditable()) {
                setInternalState(State.DELETING);
                int endLine = getCaretLine();
                int endColumn = getCaretColumn();
                int startLine = getCaretLine();
                int startColumn = getCaretColumn();
                if (startColumn == 0) {
                    if (startLine == 0) return;
                    startLine--;
                    startColumn = getEditorModel().getLineLength(startLine);
                }

                if (startColumn != 0 && startColumn - 1 != 0) {
                    while (startColumn > 0 && Character.isWhitespace(
                            getEditorModel().getChar(startLine, startColumn))) {
                        startColumn--;
                    }
                    if (startColumn != 0) {
                        boolean isIdentifier = Character.isJavaIdentifierPart(
                                getEditorModel().getChar(startLine, startColumn));
                        while (startColumn > 0) {
                            char c = getEditorModel().getChar(startLine, startColumn - 1);
                            if (isIdentifier != Character.isJavaIdentifierPart(c)
                                    || Character.isWhitespace(c)) {
                                break;
                            }
                            startColumn--;
                        }
                    }
                }
                getEditorModel().removeChars(startLine, startColumn, endLine, endColumn);
            }
        });

        boundAction(DeleteWordRight, () -> {
            if (isEditable()) {
                int startLine = getCaretLine();
                int startColumn = getCaretColumn();
                int endLine = getCaretLine();
                int endColumn = getCaretColumn();
                if (endColumn == getEditorModel().getLineLength(endLine)) {
                    if (endLine == getEditorModel().getLineCount() - 1) {
                        return;
                    }
                    int line = endLine + 1;
                    int col = 0;
                    while (col < getEditorModel().getLineLength(line) &&
                            Character.isWhitespace(getEditorModel().getChar(line, col))) {
                        col++;
                    }
                    getEditorModel().removeChars(startLine, startColumn, line - 1, col);
                    return;
                }

                if (endColumn != getEditorModel().getLineLength(endLine)) {
                    boolean isIdentifier = Character.isJavaIdentifierPart(
                            getEditorModel().getChar(endLine, endColumn));
                    while (endColumn < getEditorModel().getLineLength(endLine)) {
                        char c = getEditorModel().getChar(endLine, endColumn);
                        if (Character.isJavaIdentifierPart(c) != isIdentifier
                                || Character.isWhitespace(c)) {
                            break;
                        }
                        endColumn++;
                    }

                    while (endColumn < getEditorModel().getLineLength(endLine) &&
                            Character.isWhitespace(getEditorModel().getChar(endLine, endColumn))) {
                        endColumn++;
                    }

                }
                getEditorModel().removeChars(startLine, startColumn, endLine, endColumn - 1);
            }
        });

        boundAction(SelectAll, this::selectAll);
        boundAction(PasteSelection, this::pasteSelection);
        boundAction(CutSelection, this::cutSelection);
        boundAction(CopySelection, this::copySelection);
        boundAction(RemoveSelection, () -> {
            setInternalState(State.NONE);
            if (isEditable()) {
                moveCaret(getFirstSelectedLine(), getFirstSelectedColumn());
                removeSelection();
                setSelectionVisibility(false);
            }
        });
        boundAction(AutoIndent, this::autoIndent);
        boundAction(CommentSelection, () -> {
            setInternalState(State.NONE);
            if (isEditable())
                commentSelection();
        });
        boundAction(UnCommentSelection, () -> {
            setInternalState(State.NONE);
            if (isEditable())
                uncommentSelection();
        });
        boundAction(IndentSelection, () -> {
            setInternalState(State.NONE);
            if (isEditable())
                indentSelection();
        });
        boundAction(UnIndentSelection, () -> {
            setInternalState(State.NONE);
            if (isEditable())
                unIndentSelection();
        });
        boundAction(InsertLineBreak, this::insertLineBreak);
        boundAction(InsertTab, this::insertTab);
        boundAction(RemovePrecedingChar, () -> {
            if (isEditable()) {
                if (getSelectionVisibility()) {
                    setInternalState(State.NONE);
                    moveCaret(getFirstSelectedLine(), getFirstSelectedColumn());
                    removeSelection();
                    return;
                }
                setInternalState(State.BACKSPACING);
                int caretLine = getCaretLine();
                int caretColumn = getCaretColumn();
                if (caretColumn != 0) {
                    getEditorModel().removeChar(caretLine, caretColumn - 1);
                } else if (caretLine != 0) {
                    getEditorModel().removeLineBreak(caretLine - 1);
                }
            }
        });
        boundAction(RemoveCurrentChar, () -> {
            if (isEditable()) {
                if (getSelectionVisibility()) {
                    setInternalState(State.NONE);
                    moveCaret(getFirstSelectedLine(), getFirstSelectedColumn());
                    removeSelection();
                    setSelectionVisibility(false);
                    return;
                }
                setInternalState(State.DELETING);
                int caretLine = getCaretLine();
                int caretColumn = getCaretColumn();
                if (caretColumn > 0) {
                    getEditorModel().removeChar(caretLine, caretColumn);
                } else if (caretColumn == 0) {
                    if (caretLine == 0) return;
                    getEditorModel().removeLineBreak(caretLine);
                }
            }
        });
        boundAction(RemoveCurrentLine, this::removeCurrentLine);
        boundAction(ToggleOverwriteMode, () -> {
            setInternalState(State.NONE);
            setOverwriteMode(!isOverwriteMode());
        });
        boundAction(Undo, () -> {
            //TODO: Undo action runnable
        });

        boundAction(Redo, () -> {
            //TODO: Redo action runnable
        });
    }


    public void boundAction(@NonNull Action action, @NonNull ActionRunnable runnable) {
        myActions.put(action, runnable);
    }


    public ActionRunnable getActionRunnable(@NonNull Action action) {
        if (myActions.containsKey(action))
            return myActions.get(action);
        throw new IllegalArgumentException("unknown action:" + action);
    }

    @NonNull
    public EditorModel getEditorModel() {
        return (EditorModel) getModel();
    }

    public void setEditable(boolean editable) {
        myEditable = editable;
    }

    public boolean isEditable() {
        if (myEditable) {
            return !getEditorModel().isReadOnly();
        }
        return false;
    }

    protected void setInternalState(@NonNull State internalState) {
        if (myInternalState != internalState) {
            myInternalState = internalState;
        }
    }

    protected State getInternalState() {
        return myInternalState;
    }

    protected void setIntendedColumnX(float intendedColumnX) {
        myIntendedColumnX = intendedColumnX;
    }

    protected float getIntendedColumnX() {
        return myIntendedColumnX;
    }

    public void insertLineBreak(){
        if (isEditable()) {
            setInternalState(State.INSERTING);
            if (getSelectionVisibility()) {
                removeSelection();
                moveCaret(getFirstSelectedLine(), getLastSelectedColumn());
                setSelectionVisibility(false);
            }
            int caretLine = getCaretLine();
            int caretColumn = getCaretColumn();
            getEditorModel().insertLineBreak(caretLine, caretColumn);

        }
    }

    public void insertChar(char c) {
        if (isEditable()) {
            if (getSelectionVisibility()) {
                moveCaret(getFirstSelectedLine(), getFirstSelectedColumn());
                removeSelection();
                setSelectionVisibility(false);
            }
            if (isOverwriteMode()) {
                setInternalState(State.OVERWRITING);
                int caretLine = getCaretLine();
                int caretColumn = getCaretColumn();
                getEditorModel().insertChar(caretLine, caretColumn, c);
                moveCaret(caretLine, caretColumn + 1);
            } else {
                setInternalState(State.INSERTING);
                getEditorModel().insertChar(getCaretLine(), getCaretColumn(), c);
            }
        }
    }

    public void selectAll() {
        int lineCount = getEditorModel().getLineCount() - 1;
        int length = getEditorModel().getLineLength(lineCount);
        if (lineCount == 0 && length == 0) return;
        selection(0,0,lineCount,length);
    }

    private void removeSelection() {
        getEditorModel().removeChars(getFirstSelectedLine(), getFirstSelectedColumn(),
                getLastSelectedLine(), getLastSelectedColumn());
    }

    public void cutSelection() {
        if (isEditable()) {
            setInternalState(State.NONE);
            copySelection();
            moveCaret(getFirstSelectedLine(), getFirstSelectedColumn());
            removeSelection();
            setSelectionVisibility(false);
        }
    }

    public void copySelection() {
        if (getSelectionVisibility()) {
            Reader reader = getEditorModel().getReader(getFirstSelectedLine(),
                    getFirstSelectedColumn(), getLastSelectedLine(), getLastSelectedColumn());
            try {
                String text = IOUtils.readString(reader);
                ClipboardManager clipboardManager =
                        (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText("", text));
                reader.close();
            } catch (IOException e) {
                throw new InternalError("Exception occurred using clip board: " + e);
            }
        }
    }

    public void pasteSelection() {
        setInternalState(State.NONE);
        if (isEditable()) {
            if (getSelectionVisibility()) {
                removeSelection();
                setSelectionVisibility(false);
            }
            ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboardManager.hasPrimaryClip()) {
                if (clipboardManager.getPrimaryClip() != null && clipboardManager.getPrimaryClip().getItemCount() >= 1) {
                    ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
                    //TODO: add Lines for model
                }
            }
        }
    }

    public void autoIndent() {
        setInternalState(State.NONE);
        if (isEditable()) {
            if (getSelectionVisibility()) {
                int firstSelectedLine = getFirstSelectedLine();
                int lastSelectedLine = getLastSelectedLine();
                if (getLastSelectedColumn() == -1) {
                    lastSelectedLine--;
                }
                indentLines(firstSelectedLine, lastSelectedLine);
                updateSelect();
                return;
            }
            int length = getEditorModel().getLineLength(getCaretLine());
            int caretColumn = getCaretColumn();
            indentLines(0, getEditorModel().getLineCount() - 1);
            int column = 0;
            int length2 = caretColumn + getEditorModel().getLineLength(getCaretLine()) - length;
            if (length2 >= 0) {
                column = length2;
            }
            if (column > getEditorModel().getLineLength(getCaretLine())) {
                column = getEditorModel().getLineLength(getCaretLine());
            }
            moveCaret(getCaretLine(), column);
        }
    }

    public void indentLines(int startLine, int endLine) {
        //TODO: Program editor interface
    }

    private void updateSelect() {
        int firstSelectedLine = getFirstSelectedLine();
        int lastSelectedLine = getLastSelectedLine();
        if (getCaretLine() == firstSelectedLine) {
            if (lastSelectedLine == getEditorModel().getLineCount() - 1) {
                setSelectionAnchor(lastSelectedLine, getEditorModel().getLineLength(lastSelectedLine));
            } else if (getLastSelectedColumn() != -1) {
                setSelectionAnchor(lastSelectedLine + 1, 0);
            }
            setSelectionPoint(firstSelectedLine, 0);
            moveCaret(firstSelectedLine, 0);
        } else {
            setSelectionAnchor(firstSelectedLine, 0);
            if (lastSelectedLine == getEditorModel().getLineCount() - 1) {
                setSelectionPoint(lastSelectedLine, getEditorModel().getLineLength(lastSelectedLine));
                moveCaret(lastSelectedLine, getEditorModel().getLineLength(lastSelectedLine));
            } else if (getLastSelectedColumn() != -1) {
                setSelectionPoint(lastSelectedLine + 1, 0);
                moveCaret(lastSelectedLine + 1, 0);
            }
        }
        setSelectionVisibility(true);
    }

    public void commentSelection() {
        int firstSelectedLine = getFirstSelectedLine();
        int lastSelectedLine = getLastSelectedLine();
        if (!getSelectionVisibility()) {
            firstSelectedLine = getCaretLine();
            lastSelectedLine = firstSelectedLine;
        } else if (getLastSelectedColumn() == -1) {
            lastSelectedLine--;
        }
        commentLines(firstSelectedLine, lastSelectedLine);
        if (getCaretColumn() > getEditorModel().getLineLength(getCaretLine())) {
            moveCaret(getCaretLine(), getEditorModel().getLineLength(getCaretLine()));
        }
    }

    public void commentLines(int startLine, int endLine) {
        //TODO: Program editor interface
    }

    public void uncommentLines(int startLine, int endLine) {
        //TODO: Program editor interface
    }

    public void uncommentSelection() {
        int firstSelectedLine = getFirstSelectedLine();
        int lastSelectedLine = getLastSelectedLine();
        if (!getSelectionVisibility()) {
            firstSelectedLine = getCaretLine();
            lastSelectedLine = firstSelectedLine;
        } else if (getLastSelectedColumn() == -1) {
            lastSelectedLine--;
        }
        uncommentLines(firstSelectedLine, lastSelectedLine);
        if (getCaretColumn() > getEditorModel().getLineLength(getCaretLine())) {
            moveCaret(getCaretLine(), getEditorModel().getLineLength(getCaretLine()));
        }
    }

    public void unIndentSelection() {
        int lastSelectedLine = getLastSelectedLine();
        if (getLastSelectedColumn() == -1) {
            lastSelectedLine--;
        }
        for (int line = getFirstSelectedLine(); line <= lastSelectedLine; line++) {
            indentLine(line, getIndentationSize(line) - getTabSize());
        }
        updateSelect();
    }

    public void indentSelection() {
        int lastSelectedLine = getLastSelectedLine();
        if (getLastSelectedColumn() == -1) {
            lastSelectedLine--;
        }
        for (int line = getFirstSelectedLine(); line <= lastSelectedLine; line++) {
            indentLine(line, getIndentationSize(line) + getTabSize());
        }
        updateSelect();
    }

    public void indentLine(int line, int indentSize) {
        if (indentSize < 0) {
            indentSize = 0;
        }
        char c;
        int i = 0;
        while (i < getEditorModel().getLineLength(line) && ((c = getEditorModel().getChar(line, i)) == '\t' || c == ' ')) {
            i++;
        }
        int tabSize;
        int tabSize2;
        getEditorModel().removeChars(line, 0, i);
        if (isInsertTabsAsSpaces()) {
            tabSize = indentSize;
            tabSize2 = 0;
        } else {
            tabSize = indentSize % getTabSize();
            tabSize2 = indentSize / getTabSize();
        }
        char[] chars = new char[tabSize + tabSize2];
        for (int k = 0; k < tabSize2; k++) {
            chars[k] = '\t';
        }
        for (int j = 0; j < tabSize; j++) {
            chars[j + tabSize2] = ' ';
        }
        getEditorModel().insertChars(line, 0, chars);

    }

    public int getIndentationSize(int line) {
        if (line < 0) {
            return 0;
        }
        int size = 0;
        for (int i = 0; i < getEditorModel().getLineLength(line); i++) {
            char c = getEditorModel().getChar(line, i);
            if (c == '\t') {
                size = ((size + getTabSize()) / getTabSize()) * getTabSize();
            } else if (c != ' ') {
                return size;
            } else {
                size++;
            }
        }
        return size;

    }

    public void insertTab() {
        if (isEditable()) {
            setInternalState(State.INSERTING);
            int caretLine = getCaretLine();
            int caretColumn = getCaretColumn();
            getEditorModel().insertChar(caretLine, caretColumn, '\t');
        }
    }

    public void removeCurrentLine() {
        setInternalState(State.NONE);
        if (isEditable()) {
            int caretLine = getCaretLine();
            int line;
            if (getSelectionVisibility()) {
                int firstSelectedLine = getFirstSelectedLine();
                int lastSelectedLine = getLastSelectedLine();
                if (getLastSelectedColumn() == -1) {
                    lastSelectedLine--;
                }
                setSelectionVisibility(false);
                line = firstSelectedLine;
                caretLine = lastSelectedLine;
            } else {
                line = caretLine;
            }

            while (caretLine >= line) {
                int lineLength = getEditorModel().getLineLength(caretLine);
                if (caretLine != getEditorModel().getLineCount() - 1) {
                    moveCaret(caretLine, 0);
                    if (lineLength > 0) {
                        getEditorModel().removeChars(caretLine, 0, lineLength);
                    }
                    getEditorModel().removeLineBreak(caretLine);
                } else if (caretLine == 0) {
                    moveCaret(0, 0);
                    if (lineLength > 0) {
                        getEditorModel().removeChars(caretLine, 0, lineLength);
                    }
                } else {
                    int prevLine = caretLine - 1;
                    moveCaret(prevLine, 0);
                    if (lineLength > 0) {
                        getEditorModel().removeChars(caretLine, 0, lineLength);
                    }
                    getEditorModel().removeLineBreak(prevLine);
                }
                caretLine--;
            }
        }
    }
}
