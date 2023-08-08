package com.apkide.common.keybinding;

import static android.content.res.Configuration.KEYBOARD_NOKEYS;
import static android.view.KeyEvent.FLAG_SOFT_KEYBOARD;
import static android.view.KeyEvent.KEYCODE_ALT_LEFT;
import static android.view.KeyEvent.KEYCODE_ALT_RIGHT;
import static android.view.KeyEvent.KEYCODE_BACK;
import static android.view.KeyEvent.KEYCODE_CTRL_LEFT;
import static android.view.KeyEvent.KEYCODE_CTRL_RIGHT;
import static android.view.KeyEvent.KEYCODE_DEL;
import static android.view.KeyEvent.KEYCODE_HOME;
import static android.view.KeyEvent.KEYCODE_SEARCH;
import static android.view.KeyEvent.KEYCODE_SHIFT_LEFT;
import static android.view.KeyEvent.KEYCODE_SHIFT_RIGHT;
import static android.view.KeyEvent.KEYCODE_UNKNOWN;
import static java.lang.Character.isISOControl;
import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.InputConnection;

public class KeyStrokeDetector {
    private static final String TAG = "KeyStrokeDetector";
    private boolean altLeftDown;
    private boolean altRightDown;
    private final Context context;
    private boolean ctrlLeftDown;
    private boolean ctrlRightDown;
    private boolean isSoftKeyboard;
    private KeyCharacterMap keyCharacterMap;
    private int lastComposingTextLength;
    private boolean realShiftLeftDown;
    private boolean realShiftRightDown;
    private boolean shiftLeftDown;
    private boolean shiftRightDown;
    private boolean unknownDown;

    public interface KeyStrokeHandler {
        boolean onKeyStroke(KeyStroke keyStroke);
    }

    public KeyStrokeDetector(Context context) {
        this.context = context;
        this.isSoftKeyboard = context.getResources().getConfiguration().keyboard == KEYBOARD_NOKEYS;
        d("new KeyStrokeDetector() - isSoftKeyboard: " + this.isSoftKeyboard);
    }

    public void onConfigChange(Context context) {
        this.isSoftKeyboard = context.getResources().getConfiguration().keyboard == KEYBOARD_NOKEYS;
        d("KeyStrokeDetector.onConfigChange() - isSoftKeyboard: " + this.isSoftKeyboard);
        this.keyCharacterMap = null;
    }

    public void newWordStarted() {
        this.lastComposingTextLength = 0;
    }

    public boolean isCtrlKeyDown() {
        return this.ctrlLeftDown || this.ctrlRightDown;
    }

    public InputConnection createInputConnection(final View view, final KeyStrokeHandler keyStrokeHandler) {
        return new BaseInputConnection(view, true) {
            @Override
            public boolean performEditorAction(int actionCode) {
                KeyStrokeDetector.this.d("performEditorAction" + actionCode);
                return super.performEditorAction(actionCode);
            }

            @Override
            public boolean setComposingText(CharSequence text, int newCursorPosition) {
                KeyStrokeDetector.this.d("setComposingText '" + ((Object) text) + "'");
                for (int i = 0; i < KeyStrokeDetector.this.lastComposingTextLength; i++) {
                    keyStrokeHandler.onKeyStroke(new KeyStroke(KEYCODE_DEL, false, false, false));
                }
                KeyStrokeDetector.this.lastComposingTextLength = text.length();
                sendAsCharKeyStrokes(text, KeyStrokeDetector.this.isSoftKeyboard, keyStrokeHandler);
                return true;
            }

            @Override
            public boolean beginBatchEdit() {
                KeyStrokeDetector.this.d("beginBatchEdit");
                return super.beginBatchEdit();
            }

            @Override
            public boolean endBatchEdit() {
                KeyStrokeDetector.this.d("endBatchEdit");
                return super.endBatchEdit();
            }

            @Override
            public boolean commitCompletion(CompletionInfo text) {
                KeyStrokeDetector.this.d("commitCompletion");
                return super.commitCompletion(text);
            }

            @Override
            public boolean commitCorrection(CorrectionInfo correctionInfo) {
                KeyStrokeDetector.this.d("commitCorrection");
                return super.commitCorrection(correctionInfo);
            }

            @Override
            public boolean finishComposingText() {
                KeyStrokeDetector.this.d("finishComposingText");
                return super.finishComposingText();
            }

            @Override
            public boolean commitText(CharSequence text, int newCursorPosition) {
                KeyStrokeDetector.this.d("commitText '" + ((Object) text) + "'");
                for (int i = 0; i < KeyStrokeDetector.this.lastComposingTextLength; i++) {
                    keyStrokeHandler.onKeyStroke(new KeyStroke(KEYCODE_DEL, false, false, false));
                }
                KeyStrokeDetector.this.lastComposingTextLength = 0;
                if ("\n".equals(text.toString())) {
                    sendAsKeyEvents(text, KeyStrokeDetector.this.isSoftKeyboard, view);
                } else {
                    sendAsCharKeyStrokes(text, KeyStrokeDetector.this.isSoftKeyboard, keyStrokeHandler);
                }
                return true;
            }

            @Override
            public boolean deleteSurroundingText(int leftLength, int rightLength) {
                KeyStrokeDetector.this.d("deleteSurroundingText " + leftLength + " " + rightLength);
                KeyStrokeDetector.this.lastComposingTextLength = 0;
                for (int i = 0; i < leftLength; i++) {
                    keyStrokeHandler.onKeyStroke(new KeyStroke(KEYCODE_DEL, false, false, false));
                }
                return super.deleteSurroundingText(leftLength, rightLength);
            }

            private void sendAsCharKeyStrokes(CharSequence text, boolean isSoftKeyboard, KeyStrokeHandler strokeHandler) {
                for (int j = 0; j < text.length(); j++) {
                    char ch = text.charAt(j);
                    if (!isSoftKeyboard) {
                        if (!KeyStrokeDetector.this.realShiftLeftDown && !KeyStrokeDetector.this.realShiftRightDown) {
                            ch = toLowerCase(ch);
                        } else
                            ch = toUpperCase(ch);
                    }
                    if (strokeHandler != null)
                        strokeHandler.onKeyStroke(KeyStrokeDetector.this.makeKeyStroke(ch));
                }
            }

            private void sendAsKeyEvents(CharSequence text, boolean isSoftKeyboard, View view2) {
                if (KeyStrokeDetector.this.keyCharacterMap == null) {
                    KeyStrokeDetector.this.keyCharacterMap = KeyCharacterMap.load(0);
                }
                for (int j = 0; j < text.length(); j++) {
                    char ch = text.charAt(j);
                    if (!isSoftKeyboard) {
                        if (!KeyStrokeDetector.this.realShiftLeftDown && !KeyStrokeDetector.this.realShiftRightDown) {
                            ch = toLowerCase(ch);
                        } else {
                            ch = toUpperCase(ch);
                        }
                    }
                    char[] chars = {ch};
                    KeyEvent[] events = KeyStrokeDetector.this.keyCharacterMap.getEvents(chars);
                    if (events != null) {
                        for (KeyEvent event : events) {
                            sendKeyEvent(event);
                        }
                    }
                }
            }

            private KeyEvent transformEvent(KeyEvent event) {
                return new KeyEvent(event.getDownTime(), event.getEventTime(), event.getAction(), event.getKeyCode(), event.getRepeatCount(), event.getMetaState(), event.getDeviceId(), event.getScanCode(), event.getFlags() | 4 | 2);
            }

            @Override
            public boolean sendKeyEvent(KeyEvent event) {
                KeyStrokeDetector.this.d("sendKeyEvent " + event.getKeyCode());
                KeyStrokeDetector.this.lastComposingTextLength = 0;
                return super.sendKeyEvent(transformEvent(event));
            }

            @Override
            public CharSequence getTextBeforeCursor(int length, int flags) {
                if (isSonyEricsson()) {
                    return super.getTextBeforeCursor(length, flags);
                }
                int length2 = Math.min(length, 1024);
                StringBuilder sb = new StringBuilder(length2);
                for (int i = 0; i < length2; i++) {
                    sb.append(' ');
                }
                return sb;
            }

            private boolean isSonyEricsson() {
                String defaultInputMethodName = null;
                try {
                    defaultInputMethodName = Settings.Secure.getString(KeyStrokeDetector.this.context.getContentResolver(), "default_input_method");
                    KeyStrokeDetector.this.d("Default IME: " + defaultInputMethodName);
                } catch (Exception ignored) {
                }
                return defaultInputMethodName != null && defaultInputMethodName.startsWith("com.sonyericsson.");
            }
        };
    }

    public boolean onKeyDown(int keyCode, KeyEvent event, KeyStrokeHandler keyStrokeHandler) {
        debugOnKey("onKeyDown", keyCode, event);
        if (keyCode == KEYCODE_SEARCH) {
            keyCode = KEYCODE_ALT_LEFT;
        }
        handleMetaKeysDown(keyCode, (event.getFlags() & 2) != 0);
        KeyStroke keyStroke = makeKeyStroke(keyCode, event);
        if (keyStroke == null || !keyStrokeHandler.onKeyStroke(keyStroke)) {
            return false;
        }
        debugOnKeyStroke(keyStroke);
        return true;
    }

    public void d(String msg) {
        Log.d(TAG, msg);
    }

    private void debugOnKeyStroke(KeyStroke keyStroke) {
        d("onKeyStroke " + keyStroke.toString());
    }

    private void debugOnKey(String method, int keyCode, KeyEvent event) {
        d(method + " " + keyCode + "  " + event.getFlags() + (event.isAltPressed() ? " alt" : "") + (event.isShiftPressed() ? " shift" : "") + " " + (isCtrl(event.getMetaState()) ? " ctrl" : ""));
    }

    private boolean isCtrl(int metaState) {
        return (metaState & 12288) != 0;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        debugOnKey("onKeyUp", keyCode, event);
        if (keyCode == KEYCODE_SEARCH) {
            keyCode = KEYCODE_ALT_LEFT;
        }
        handleMetaKeysUp(keyCode, (event.getFlags() & FLAG_SOFT_KEYBOARD) != 0);
        return false;
    }

    public KeyStroke makeKeyStroke(char ch) {
        return new KeyStroke(-1, ch, isUpperCase(ch) | this.realShiftLeftDown | this.realShiftRightDown, false, false);
    }

    private KeyStroke makeKeyStroke(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KEYCODE_UNKNOWN:
            case KEYCODE_HOME:
            case KEYCODE_BACK:
            case KEYCODE_ALT_LEFT:
            case KEYCODE_ALT_RIGHT:
            case KEYCODE_SHIFT_LEFT:
            case KEYCODE_SHIFT_RIGHT:
            case KEYCODE_CTRL_LEFT:
            case KEYCODE_CTRL_RIGHT:
                return null;
            default:
                boolean shift = this.shiftLeftDown | this.shiftRightDown | event.isShiftPressed();
                boolean ctrl = this.ctrlLeftDown | this.ctrlRightDown | isCtrl(event.getMetaState());
                boolean alt = this.altLeftDown | this.altRightDown | event.isAltPressed();
                char ch = 65535;
                int chi = event.getUnicodeChar();
                if (chi != 0 && !isISOControl(chi)) {
                    ch = (char) chi;
                }
                return new KeyStroke(keyCode, ch, shift, ctrl, alt);
        }
    }

    public void onActivityKeyUp(int keyCode, KeyEvent event) {
        handleMetaKeysUp(keyCode, (event.getFlags() & FLAG_SOFT_KEYBOARD) != 0);
    }

    public void onActivityKeyDown(int keyCode, KeyEvent event) {
        handleMetaKeysDown(keyCode, (event.getFlags() & FLAG_SOFT_KEYBOARD) != 0);
    }

    private void handleMetaKeysDown(int keyCode, boolean isSoftKeyboard) {
        d("onMetaKeysDown " + keyCode);
        this.altLeftDown = (keyCode == KEYCODE_ALT_LEFT) | this.altLeftDown;
        this.altRightDown = (keyCode == KEYCODE_ALT_RIGHT) | this.altRightDown;
        this.shiftLeftDown = (keyCode == KEYCODE_SHIFT_LEFT) | this.shiftLeftDown;
        this.shiftRightDown = (keyCode == KEYCODE_SHIFT_RIGHT) | this.shiftRightDown;
        this.realShiftLeftDown = (keyCode == KEYCODE_SHIFT_LEFT && !isSoftKeyboard) | this.realShiftLeftDown;
        this.realShiftRightDown = (keyCode == KEYCODE_SHIFT_RIGHT && !isSoftKeyboard) | this.realShiftRightDown;
        this.unknownDown = (keyCode == KEYCODE_UNKNOWN) | this.unknownDown;
        this.ctrlLeftDown = (keyCode == KEYCODE_CTRL_LEFT) | this.ctrlLeftDown;
        this.ctrlRightDown |= keyCode == KEYCODE_CTRL_RIGHT;
    }

    private void handleMetaKeysUp(int keyCode, boolean isSoftKeyboard) {
        d("onMetaKeysUp " + keyCode);
        this.altLeftDown = (keyCode != KEYCODE_ALT_LEFT) & this.altLeftDown;
        this.altRightDown = (keyCode != KEYCODE_ALT_RIGHT) & this.altRightDown;
        this.shiftLeftDown = (keyCode != KEYCODE_SHIFT_LEFT) & this.shiftLeftDown;
        this.shiftRightDown = (keyCode != KEYCODE_SHIFT_RIGHT) & this.shiftRightDown;
        this.realShiftLeftDown = (keyCode != KEYCODE_SHIFT_LEFT || isSoftKeyboard) & this.realShiftLeftDown;
        this.realShiftRightDown = (keyCode != KEYCODE_SHIFT_RIGHT || isSoftKeyboard) & this.realShiftRightDown;
        this.unknownDown = (keyCode != KEYCODE_UNKNOWN) & this.unknownDown;
        this.ctrlLeftDown = (keyCode != KEYCODE_CTRL_LEFT) & this.ctrlLeftDown;
        this.ctrlRightDown &= keyCode != KEYCODE_CTRL_RIGHT;
    }
}
