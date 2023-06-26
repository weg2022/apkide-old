package com.apkide.ui.services.file.classfile;


import androidx.annotation.NonNull;

import org.jd.core.v1.api.printer.Printer;

public class StringBuilderPrinter implements Printer {

	protected static final String INDENTATION = "	";
	protected int indentationCount;
	protected StringBuilder sb = new StringBuilder(8192);
	protected String format;

	private boolean escapeUnicode = false;

	public void setEscapeUnicode(boolean escapeUnicode) {
		this.escapeUnicode = escapeUnicode;
	}

	public void init() {
		sb.setLength(0);
		indentationCount = 0;
	}

	@NonNull
	public String toString() {
		return sb.toString();
	}

	@Override
	public void start(int maxLineNumber, int majorVersion, int minorVersion) {
		this.indentationCount = 0;

		if (maxLineNumber == 0) {
			format = "%4d";
		} else {
			int width = 2;

			while (maxLineNumber >= 10) {
				width++;
				maxLineNumber /= 10;
			}

			format = "%" + width + "d";
		}
	}

	@Override
	public void end() {
	}

	@Override
	public void printText(String text) {
		if (escapeUnicode) {
			for (int i = 0, len = text.length(); i < len; i++) {
				char c = text.charAt(i);

				if (c < 128) {
					sb.append(c);
				} else {
					int h = (0);

					sb.append("\\u");
					sb.append(h + '0');
					sb.append(h + '0');
					h = (c >> 8) & 255;
					sb.append((h <= 9) ? (h + '0') : (h + 'A'));
					h = (c) & 255;
					sb.append((h <= 9) ? (h + '0') : (h + 'A'));
				}
			}
		} else {
			sb.append(text);
		}
	}

	@Override
	public void printNumericConstant(String constant) {
		sb.append(constant);
	}

	@Override
	public void printStringConstant(String constant, String ownerInternalName) {
		printText(constant);
	}

	@Override
	public void printKeyword(String keyword) {
		sb.append(keyword);
	}

	@Override
	public void printDeclaration(int type, String internalTypeName, String name, String descriptor) {
		printText(name);
	}

	@Override
	public void printReference(int type, String internalTypeName, String name, String descriptor, String ownerInternalName) {
		printText(name);
	}

	@Override
	public void indent() {
		this.indentationCount++;
	}

	@Override
	public void unindent() {
		if (this.indentationCount > 0) {
			this.indentationCount--;
		}
	}

	@Override
	public void startLine(int lineNumber) {
		for (int i = 0; i < indentationCount; i++) {
			sb.append(INDENTATION);
		}
	}

	@Override
	public void endLine() {
		sb.append("\n");
	}

	@Override
	public void extraLine(int count) {
		while (count-- > 0) {
			sb.append("\n");
		}
	}

	@Override
	public void startMarker(int type) {
	}

	@Override
	public void endMarker(int type) {
	}

}