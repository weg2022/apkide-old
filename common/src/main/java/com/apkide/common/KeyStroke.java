package com.apkide.common;

import android.view.KeyEvent;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class KeyStroke implements Serializable {
    private static final long serialVersionUID = -5001727409260147797L;
    public static final char CHAR_UNDEFINED = 0xFFFF;
    private final int keyCode;
    private final char keyChar;
    private final boolean shift;
    private final boolean ctrl;
    private final boolean alt;
    
    public KeyStroke(int keyCode, boolean shift, boolean ctrl, boolean alt) {
        this(keyCode, CHAR_UNDEFINED, shift, ctrl, alt);
    }
    
    public KeyStroke(int keyCode, char keyChar, boolean shift, boolean ctrl, boolean alt) {
        this.keyCode = keyCode;
        this.keyChar = keyChar;
        this.alt = alt;
        this.ctrl = ctrl;
        this.shift = shift;
    }
    
    public boolean isChar() {
        return keyChar != CHAR_UNDEFINED;
    }
    
    public boolean isCtrlPressed() {
        return ctrl;
    }
    
    public boolean isShiftPressed() {
        return shift;
    }
    
    public boolean isAltPressed() {
        return alt;
    }
    
    public int getKeyCode() {
        return keyCode;
    }
    
    public char getKeyChar() {
        return keyChar;
    }
    
    public boolean match(@NonNull KeyStroke key) {
        if (key.keyCode == keyCode && key.shift == shift && key.ctrl == ctrl && key.alt == alt) {
            if (isChar()) {
                return key.keyChar == keyChar;
            }
            return true;
        }
        return false;
    }
    
    @NonNull
    public String store() {
        return keyCode + "," + keyChar + "," + shift + "," + ctrl + "," + alt;
    }
    
    @NonNull
    public static KeyStroke make(@NonNull KeyEvent event) {
        return new KeyStroke(
                event.getKeyCode(),
                event.getUnicodeChar() == 0 || Character.isISOControl(event.getUnicodeChar()) ?
                        CHAR_UNDEFINED : (char) event.getUnicodeChar(),
                event.isShiftPressed(),
                event.isCtrlPressed(),
                event.isAltPressed());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        KeyStroke keyStroke = (KeyStroke) o;
        
        if (keyCode != keyStroke.keyCode) return false;
        if (keyChar != keyStroke.keyChar) return false;
        if (shift != keyStroke.shift) return false;
        if (ctrl != keyStroke.ctrl) return false;
        return alt == keyStroke.alt;
    }
    
    @Override
    public int hashCode() {
        int result = keyCode;
        result = 31 * result + (int) keyChar;
        result = 31 * result + (shift ? 1 : 0);
        result = 31 * result + (ctrl ? 1 : 0);
        result = 31 * result + (alt ? 1 : 0);
        return result;
    }
}
