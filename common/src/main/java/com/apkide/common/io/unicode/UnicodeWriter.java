package com.apkide.common.io.unicode;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class UnicodeWriter extends Writer {
	
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
	private final OutputStreamWriter writer;
	
	public UnicodeWriter(@NonNull OutputStream out, @NonNull String encoding) throws IOException {
		this(out, encoding, true);
	}
	
	public UnicodeWriter(@NonNull OutputStream out, @NonNull String encoding, boolean writeUtf8BOM) throws IOException {
		writer = new OutputStreamWriter(out, encoding);
		if (encoding.equalsIgnoreCase("UTF-8")) {
			if (writeUtf8BOM)
				out.write(BOM[UTF8_BOM], 0, BOM[UTF8_BOM].length);
		} else if (encoding.equalsIgnoreCase("UTF-16LE"))
			out.write(BOM[UTF16LE_BOM], 0, BOM[UTF16LE_BOM].length);
		else if (encoding.equalsIgnoreCase("UTF-16BE"))
			out.write(BOM[UTF16BE_BOM], 0, BOM[UTF16BE_BOM].length);
		else if (encoding.equalsIgnoreCase("UTF-32LE"))
			out.write(BOM[UTF32LE_BOM], 0, BOM[UTF32LE_BOM].length);
		else if (encoding.equalsIgnoreCase("UTF-32BE"))
			out.write(BOM[UTF32BE_BOM], 0, BOM[UTF32BE_BOM].length);
	}
	
	@NonNull
	public String getEncoding() {
		return writer.getEncoding();
	}
	
	@Override
	public void write(int c) throws IOException {
		writer.write(c);
	}
	
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		writer.write(cbuf, off, len);
	}
	
	@Override
	public void write(String str, int off, int len) throws IOException {
		writer.write(str, off, len);
	}
	
	@Override
	public void flush() throws IOException {
		writer.flush();
	}
	
	@Override
	public void close() throws IOException {
		writer.close();
	}
	
	
}
