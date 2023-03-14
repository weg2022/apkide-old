package com.apkide.common;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.util.Arrays;

public class UnicodeReader extends Reader {
	private static final byte[][] BOM = new byte[][]{
			{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF},
			{(byte) 0xFF, (byte) 0xFE},
			{(byte) 0xFE, (byte) 0xFF},
			{(byte) 0xFF, (byte) 0xFE, (byte) 0x00, (byte) 0x00},
			{(byte) 0x00, (byte) 0x00, (byte) 0xFE, (byte) 0xFF}
	};
	
	private static final int UTF8_BOM = 0;
	private static final int UTF16LE_BOM = 1;
	private static final int UTF16BE_BOM = 2;
	private static final int UTF32LE_BOM = 3;
	private static final int UTF32BE_BOM = 4;
	
	private final InputStreamReader reader;
	private String encoding;
	
	public UnicodeReader(InputStream in, String defaultEncoding) throws IOException {
		PushbackInputStream tempIn = new PushbackInputStream(in, 4);
		byte[] temp = new byte[4];
		int unread;
		int len = tempIn.read(temp, 0, temp.length);
		if ( Arrays.equals(temp, BOM[UTF32LE_BOM])) {
			encoding = "UTF-32LE";
			unread = len - 4;
		} else if (Arrays.equals(temp, BOM[UTF32BE_BOM])) {
			encoding = "UTF-32BE";
			unread = len - 4;
		} else if (temp[0] == BOM[UTF8_BOM][0] && temp[1] == BOM[UTF8_BOM][1] && temp[2] == BOM[UTF8_BOM][2]) {
			encoding = "UTF-8";
			unread = len - 3;
		} else if (temp[0] == BOM[UTF16LE_BOM][0] && temp[1] == BOM[UTF16LE_BOM][1]) {
			encoding = "UTF-16LE";
			unread = len - 2;
		} else if (temp[0] == BOM[UTF16BE_BOM][0] && temp[1] == BOM[UTF16BE_BOM][1]) {
			encoding = "UTF-16BE";
			unread = len - 2;
		} else {
			encoding = defaultEncoding;
			unread = len;
		}
		
		
		if (unread > 0) {
			tempIn.unread(temp, 0, unread);
		} else if (unread < -1)
			tempIn.unread(temp, 0, 0);
		
		if (encoding == null) {
			reader = new InputStreamReader(in);
			encoding = reader.getEncoding();
		} else
			reader = new InputStreamReader(in, encoding);
	}
	
	@NonNull
	public String getEncoding() {
		return encoding;
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		return reader.read(cbuf, off, len);
	}
	
	@Override
	public void close() throws IOException {
		reader.close();
	}
}
