package com.apkide.common;

public class KeyStroke {
	public static final char CHAR_UNDEFINED = 0xFFFF;
	public int keyCode;
	public char keyChar;
	public boolean shift;
	public boolean ctrl;
	public boolean alt;

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

	public boolean match(KeyStroke key) {
		if (key == null) return false;
		if (key.keyCode == keyCode && key.shift == shift && key.ctrl == ctrl && key.alt == alt) {
			if (isChar()) {
				return key.keyChar == keyChar;
			}
			return true;
		}
		return false;
	}

	public String store() {
		return keyCode + "," + keyChar + "," + shift + "," + ctrl + "," + alt;
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
