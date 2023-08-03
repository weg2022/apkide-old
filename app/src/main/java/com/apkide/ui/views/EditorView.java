package com.apkide.ui.views;

import static android.view.KeyEvent.KEYCODE_BUTTON_A;
import static android.view.KeyEvent.KEYCODE_DPAD_CENTER;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static java.lang.Math.max;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.apkide.common.AppLog;
import com.apkide.common.KeyStroke;
import com.apkide.common.KeyStrokeDetector;
import com.apkide.ui.views.editor.Editor;

import java.util.List;

public class EditorView extends Editor {
    private final WindowManager windowManager;
    private final KeyStrokeDetector.KeyStrokeHandler myKeyStrokeHandler;

    public EditorView(Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setCaretVisibility(true);
        myKeyStrokeHandler = new KeyStrokeDetector.KeyStrokeHandler() {

            private KeyStrokeCommand foundCommand(KeyStroke key) {
                List<KeyStrokeCommand> commands = getCodeEditText().getEditorCommands();
                for (KeyStrokeCommand command : commands) {
                    List<KeyStroke> keys = getCodeEditText().foundKeys(command);
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
                int keyCode = keyStroke.getKeyCode();
                if (keyCode != KEYCODE_BUTTON_A && keyCode != KEYCODE_DPAD_CENTER) {
                    KeyStrokeCommand command = foundCommand(keyStroke);
                    if (command != null) {
                        if (command.isEnabled()) {
                            command.run();
                            return true;
                        }
                    } else if (keyStroke.isChar()) {
                        insertChar(keyStroke.getChar());
                        return true;
                    } else if (keyCode == KEYCODE_ENTER) {
                        insertLineBreak();
                        return true;
                    } else {
                        return getCodeEditText().handleKeyStroke(keyStroke);
                    }
                }
                getCodeEditText().onKeyStroke();
                return true;
            }
        };
    }

    public CodeEditText.CodeEditTextEditorModel getCodeEditTextEditorModel() {
        return (CodeEditText.CodeEditTextEditorModel) getEditorModel();
    }

    public KeyStrokeDetector.KeyStrokeHandler getKeyStrokeHandler() {
        return myKeyStrokeHandler;
    }

    private CodeEditText getCodeEditText(){
        return (CodeEditText) getParent();
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
    public boolean onCheckIsTextEditor() {
        return true;
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        if (getCodeEditText().getKeyStrokeDetector() != null) {
            return getCodeEditText().getKeyStrokeDetector().createInputConnection(this, myKeyStrokeHandler);
        }
        return super.onCreateInputConnection(outAttrs);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getCodeEditText().getKeyStrokeDetector() != null) {
            if (getCodeEditText().getKeyStrokeDetector().onKeyDown(keyCode, event, myKeyStrokeHandler)) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (getCodeEditText().getKeyStrokeDetector() != null) {
            if (getCodeEditText().getKeyStrokeDetector().onKeyUp(keyCode, event)) {
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
