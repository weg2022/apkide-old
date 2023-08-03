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
import static java.lang.Math.max;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.common.AppLog;
import com.apkide.common.KeyStroke;
import com.apkide.common.KeyStrokeDetector;
import com.apkide.common.Styles;
import com.apkide.common.SyntaxKind;
import com.apkide.ui.views.editor.Action;
import com.apkide.ui.views.editor.Editor;
import com.apkide.ui.views.editor.EditorModel;
import com.apkide.ui.views.editor.Model;
import com.apkide.ui.views.editor.ModelListener;
import com.apkide.ui.views.editor.TextStyle;

import java.util.ArrayList;
import java.util.List;

public class CodeEditText extends ViewGroup {
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


    public class EditorView extends Editor {
        private final WindowManager windowManager;
        private final KeyStrokeDetector.KeyStrokeHandler myKeyStrokeHandler;

        public EditorView(Context context) {
            super(context);
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            setFocusable(true);
            setFocusableInTouchMode(true);
            setNextFocusUpId(CodeEditText.this.getNextFocusDownId());
            setCaretVisibility(true);
            myKeyStrokeHandler = new KeyStrokeDetector.KeyStrokeHandler() {
                @Override
                public boolean onKeyStroke(KeyStroke keyStroke) {
                    return false;
                }
            };
        }

        public KeyStrokeDetector.KeyStrokeHandler getKeyStrokeHandler() {
            return myKeyStrokeHandler;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int width;
            int height;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                width = windowManager.getCurrentWindowMetrics().getBounds().width();
                height = windowManager.getCurrentWindowMetrics().getBounds().height();
            } else {
                width = windowManager.getDefaultDisplay().getWidth();
                height = windowManager.getDefaultDisplay().getHeight();
            }
            setMeasuredDimension(max(getMeasuredWidth(), width), max(getMeasuredHeight(), height));
            AppLog.d("w:" + getMeasuredWidth() + " h:" + getMeasuredHeight());
        }

        @Override
        public void indentLines(int startLine, int endLine) {

        }

        @Override
        public boolean onCheckIsTextEditor() {
            return true;
        }

        @Override
        public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
            if (getKeyStrokeDetector() != null) {
                return getKeyStrokeDetector().createInputConnection(this, myKeyStrokeHandler);
            }
            return super.onCreateInputConnection(outAttrs);
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            if (getKeyStrokeDetector() != null) {
                if (getKeyStrokeDetector().onKeyDown(keyCode, event, myKeyStrokeHandler)) {
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
    }

    protected class CodeEditTextEditorModel extends EditorModel {
        private final Object myStylesLock = new Object();
        private Styles myStyles = new Styles();
        private Styles myStylesGUI = new Styles();
        private final Object mySemanticStylesLock = new Object();
        private Styles mySemanticsStyles = new Styles();
        private Styles mySemanticsStylesGUI = new Styles();

        public CodeEditTextEditorModel() {
            super();
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
            return super.getTextStyle(style);
        }


        public void highlighting(SyntaxKind[] kinds, int[] startLines, int[] startColumns, int[] endLines, int[] endColumns, int size) {
            myStyles.set(kinds, startLines, startColumns, endLines, endColumns, size);
            synchronized (myStylesLock) {
                Styles styles = myStylesGUI;
                myStylesGUI = myStyles;
                myStyles = styles;
            }
            postInvalidate();
        }

        public void semanticHighlighting(SyntaxKind[] kinds, int[] startLines, int[] startColumns, int[] endLines, int[] endColumns, int size) {
            mySemanticsStyles.set(kinds, startLines, startColumns, endLines, endColumns, size);
            synchronized (mySemanticStylesLock) {
                Styles styles = mySemanticsStylesGUI;
                mySemanticsStylesGUI = mySemanticsStyles;
                mySemanticsStyles = styles;
            }
            postInvalidate();
        }
    }


    private KeyStrokeDetector myKeyStrokeDetector;
    private final List<KeyStrokeCommand> myEditorCommands = new ArrayList<>();

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

    }


    public void putEditorCommand(KeyStrokeCommand command) {
        myEditorCommands.remove(command);
        myEditorCommands.add(command);
    }

    public List<KeyStrokeCommand> getEditorCommands() {
        return myEditorCommands;
    }

    public void setKeyStrokeDetector(KeyStrokeDetector keyStrokeDetector) {
        myKeyStrokeDetector = keyStrokeDetector;
    }

    public KeyStrokeDetector getKeyStrokeDetector() {
        return myKeyStrokeDetector;
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


}
