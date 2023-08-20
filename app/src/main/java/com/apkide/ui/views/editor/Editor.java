package com.apkide.ui.views.editor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

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
    
    public enum State {
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
    
    private boolean myInSelecting;
    
    private void initView() {
    
    }
    
    
    public EditorModel getEditorModel() {
        return (EditorModel) super.getModel();
    }
    
    public void setEditable(boolean editable) {
        myEditable = editable;
    }
    
    public boolean isEditable() {
        return myEditable && !getModel().isReadOnly();
    }
    
    public void setInternalState(@NonNull State internalState) {
        if (myInternalState != internalState) {
            myInternalState = internalState;
        }
    }
    
    public State getInternalState() {
        return myInternalState;
    }
    
    public void setIntendedColumnX(float intendedColumnX) {
        myIntendedColumnX = intendedColumnX;
    }
    
    public float getIntendedColumnX() {
        return myIntendedColumnX;
    }
    
    public void runOnSelect(Runnable runnable) {
        if (!myInSelecting)
            setSelectionAnchor(getCaretLine(), getCaretColumn());
        myInSelecting = true;
        runnable.run();
        myInSelecting = false;
        setSelectionPoint(getCaretLine(), getCaretColumn());
        setSelectionVisibility(true);
    }
    
    public void runOnUnSelect(Runnable runnable) {
        if (isSelectionVisibility() && !myInSelecting)
            setSelectionVisibility(false);
        runnable.run();
    }
    
    
    public void moveCaretLeft() {
        runOnUnSelect(() -> {
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
        });
    }
    
    public void moveCaretRight() {
        runOnUnSelect(() -> {
            setInternalState(State.NONE);
            int caretLine = getCaretLine();
            int caretColumn = getCaretColumn();
            if (caretColumn != getEditorModel().getLineLength(caretLine))
                moveCaret(caretLine, caretColumn + 1);
            else if (caretLine != getEditorModel().getLineCount() - 1)
                moveCaret(caretLine + 1, 0);
        });
    }
    
    public void moveCaretUp() {
        runOnUnSelect(() -> {
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
        });
    }
    
    public void moveCaretDown() {
        runOnUnSelect(() -> {
            int caretLine = getCaretLine();
            int caretColumn = getCaretColumn();
            setInternalState(State.MOVING_VERTICALLY);
            setIntendedColumnX(computeLocationX(caretLine, caretColumn));
            caretLine++;
            if (caretLine != getEditorModel().getLineCount())
                moveCaret(caretLine, computeColumn(caretLine, getIntendedColumnX()));
        });
    }
    
    public void moveCaretPageUp() {
        runOnUnSelect(() -> {
            int caretLine = getCaretLine();
            int caretColumn = getCaretColumn();
            setInternalState(State.MOVING_VERTICALLY);
            setIntendedColumnX(computeLocationX(caretLine, caretColumn));
            for (int i = 0; caretLine > 0 && i < 10; i++) {
                caretLine--;
            }
            moveCaret(caretLine, computeColumn(caretLine, getIntendedColumnX()));
        });
    }
    
    public void moveCaretPageDown() {
        runOnUnSelect(() -> {
            int caretLine = getCaretLine();
            int caretColumn = getCaretColumn();
            setInternalState(State.MOVING_VERTICALLY);
            setIntendedColumnX(computeLocationX(caretLine, caretColumn));
            for (int i = 0; caretLine > 0 && i < 10; i++) {
                caretLine--;
            }
            moveCaret(caretLine, computeColumn(caretLine, getIntendedColumnX()));
        });
    }
    
    public void moveCaretLeftSelect() {
        runOnSelect(() -> {
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
        });
    }
    
    public void moveCaretRightSelect() {
        runOnSelect(() -> {
            setInternalState(State.NONE);
            int caretLine = getCaretLine();
            int caretColumn = getCaretColumn();
            if (caretColumn != getEditorModel().getLineLength(caretLine))
                moveCaret(caretLine, caretColumn + 1);
            else if (caretLine != getEditorModel().getLineCount() - 1)
                moveCaret(caretLine + 1, 0);
        });
    }
    
    public void moveCaretUpSelect() {
        runOnSelect(() -> {
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
        });
    }
    
    public void moveCaretDownSelect() {
        runOnSelect(() -> {
            int caretLine = getCaretLine();
            int caretColumn = getCaretColumn();
            setInternalState(State.MOVING_VERTICALLY);
            setIntendedColumnX(computeLocationX(caretLine, caretColumn));
            caretLine++;
            if (caretLine != getEditorModel().getLineCount())
                moveCaret(caretLine, computeColumn(caretLine, getIntendedColumnX()));
        });
    }
    
    public void moveCaretPageUpSelect() {
        runOnSelect(() -> {
            int caretLine = getCaretLine();
            int caretColumn = getCaretColumn();
            setInternalState(State.MOVING_VERTICALLY);
            setIntendedColumnX(computeLocationX(caretLine, caretColumn));
            for (int i = 0; caretLine > 0 && i < 10; i++) {
                caretLine--;
            }
            moveCaret(caretLine, computeColumn(caretLine, getIntendedColumnX()));
        });
    }
    
    public void moveCaretPageDownSelect() {
        runOnSelect(() -> {
            int caretLine = getCaretLine();
            int caretColumn = getCaretColumn();
            setInternalState(State.MOVING_VERTICALLY);
            setIntendedColumnX(computeLocationX(caretLine, caretColumn));
            for (int i = 0; caretLine > 0 && i < 10; i++) {
                caretLine--;
            }
            moveCaret(caretLine, computeColumn(caretLine, getIntendedColumnX()));
        });
    }
    
    public void insertLineBreak() {
        if (isEditable()) {
            setInternalState(State.INSERTING);
            if (isSelectionVisibility()) {
                removeSelectionText();
                moveCaret(getFirstSelectedLine(), getLastSelectedColumn());
                setSelectionVisibility(false);
            }
            int caretLine = getCaretLine();
            int caretColumn = getCaretColumn();
            getEditorModel().insertLineBreak(caretLine, caretColumn);
            
        }
    }
    
    public void insertTab() {
        if (isEditable()) {
            setInternalState(State.INSERTING);
            int caretLine = getCaretLine();
            int caretColumn = getCaretColumn();
            getEditorModel().insert(caretLine, caretColumn, '\t');
        }
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
    
    public void indentLine(int line, int indentSize) {
        if (indentSize < 0) {
            indentSize = 0;
        }
        char c;
        int column = 0;
        int length = getEditorModel().getLineLength(line);
        
        while (column < length && ((c = getEditorModel().getChar(line, column)) == '\t' || c == ' ')) {
            column++;
        }
        int spaceCount;
        int tabCount;
        getEditorModel().remove(line, 0, line, column);
        if (isInsertTabsAsSpaces()) {
            spaceCount = indentSize;
            tabCount = 0;
        } else {
            spaceCount = indentSize % getTabSize();
            tabCount = indentSize / getTabSize();
        }
        
        StringBuilder text = new StringBuilder();
        
        for (int k = 0; k < tabCount; k++) {
            text.append("\t");
        }
        for (int j = 0; j < spaceCount; j++) {
            text.append(" ");
        }
        getEditorModel().insert(line, 0, text.toString());
        
    }
    
    public void commentLines(int startLine, int endLine) {
        //TODO: Program editor interface
    }
    
    public void uncommentLines(int startLine, int endLine) {
        //TODO: Program editor interface
    }
    
    public void uncommentSelection() {
        setInternalState(State.NONE);
        if (isEditable()) {
            int firstSelectedLine = getFirstSelectedLine();
            int lastSelectedLine = getLastSelectedLine();
            if (!isSelectionVisibility()) {
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
    }
    
    public void unIndentSelection() {
        setInternalState(State.NONE);
        if (isEditable()) {
            int lastSelectedLine = getLastSelectedLine();
            if (getLastSelectedColumn() == -1) {
                lastSelectedLine--;
            }
            for (int line = getFirstSelectedLine(); line <= lastSelectedLine; line++) {
                indentLine(line, getIndentationSize(line) - getTabSize());
            }
            updateSelect();
        }
    }
    
    public void indentSelection() {
        setInternalState(State.NONE);
        if (isEditable()) {
            int lastSelectedLine = getLastSelectedLine();
            if (getLastSelectedColumn() == -1) {
                lastSelectedLine--;
            }
            for (int line = getFirstSelectedLine(); line <= lastSelectedLine; line++) {
                indentLine(line, getIndentationSize(line) + getTabSize());
            }
            updateSelect();
        }
    }
    
    public void insertChar(char c) {
        if (isEditable()) {
            if (isSelectionVisibility()) {
                moveCaret(getFirstSelectedLine(), getFirstSelectedColumn());
                removeSelectionText();
            }
            int caretLine = getCaretLine();
            int caretColumn = getCaretColumn();
            
            if (isOverwriteMode()) {
                setInternalState(State.OVERWRITING);
                getEditorModel().insert(caretLine, caretColumn, c);
                moveCaret(caretLine, caretColumn);
            } else {
                setInternalState(State.INSERTING);
                getEditorModel().insert(caretLine, caretColumn, c);
                moveCaret(caretLine, caretColumn+1);
            }
        }
    }
    
    public void selectAll() {
        int lineCount = getEditorModel().getLineCount() - 1;
        int length = getEditorModel().getLineLength(lineCount);
        if (lineCount == 0 && length == 0) return;
        selection(0, 0, lineCount, length);
    }
    
    public void removeSelectionText() {
        setInternalState(State.NONE);
        if (isEditable()) {
            moveCaret(getFirstSelectedLine(), getFirstSelectedColumn());
            getEditorModel().remove(getFirstSelectedLine(), getFirstSelectedColumn(),
                    getLastSelectedLine(), getLastSelectedColumn());
            setSelectionVisibility(false);
        }
    }
    
    public void cutSelection() {
        if (isEditable()) {
            setInternalState(State.NONE);
            copySelection();
            moveCaret(getFirstSelectedLine(), getFirstSelectedColumn());
            removeSelectionText();
            setSelectionVisibility(false);
        }
    }
    
    public void copySelection() {
        if (isSelectionVisibility()) {
            String text = getEditorModel().getText(getFirstSelectedLine(),
                    getFirstSelectedColumn(), getLastSelectedLine(), getLastSelectedColumn());
            ClipboardManager clipboardManager =
                    (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setPrimaryClip(ClipData.newPlainText("", text));
        }
    }
    
    public void pasteSelection() {
        setInternalState(State.NONE);
        if (isEditable()) {
            if (isSelectionVisibility()) {
                removeSelectionText();
                setSelectionVisibility(false);
            }
            ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboardManager.hasPrimaryClip()) {
                if (clipboardManager.getPrimaryClip() != null && clipboardManager.getPrimaryClip().getItemCount() >= 1) {
                    ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
                    getEditorModel().insert(getCaretLine(), getCaretColumn(), item.getText().toString());
                }
            }
        }
    }
    
    public void autoIndent() {
        setInternalState(State.NONE);
        if (isEditable()) {
            if (isSelectionVisibility()) {
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
        setInternalState(State.NONE);
        if (isEditable()) {
            int firstSelectedLine = getFirstSelectedLine();
            int lastSelectedLine = getLastSelectedLine();
            if (!isSelectionVisibility()) {
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
    }
    
    public void toggleOverwrite() {
        setInternalState(State.NONE);
        setOverwriteMode(!isOverwriteMode());
    }
    
    public void removePreChar() {
        if (isEditable()) {
            if (isSelectionVisibility()) {
                setInternalState(State.NONE);
                moveCaret(getFirstSelectedLine(), getFirstSelectedColumn());
                removeSelectionText();
                return;
            }
            setInternalState(State.BACKSPACING);
            int caretLine = getCaretLine();
            int caretColumn = getCaretColumn();
            if (caretColumn != 0) {
                getEditorModel().remove(caretLine, caretColumn - 1, caretLine, caretColumn);
            } else if (caretLine != 0) {
                getEditorModel().removeLineBreak(caretLine - 1);
            }
        }
    }
    
    public void removeChar() {
        if (isEditable()) {
            if (isSelectionVisibility()) {
                setInternalState(State.NONE);
                moveCaret(getFirstSelectedLine(), getFirstSelectedColumn());
                removeSelectionText();
                setSelectionVisibility(false);
                return;
            }
            setInternalState(State.DELETING);
            int caretLine = getCaretLine();
            int caretColumn = getCaretColumn();
            if (caretColumn > 0) {
                getEditorModel().remove(caretLine, caretColumn, caretLine, caretColumn + 1);
            } else if (caretColumn == 0) {
                if (caretLine == 0) return;
                getEditorModel().removeLineBreak(caretLine);
            }
        }
    }
}
