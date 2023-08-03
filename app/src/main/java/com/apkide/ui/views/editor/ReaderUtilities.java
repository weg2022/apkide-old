package com.apkide.ui.views.editor;

import com.apkide.common.AppLog;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

public class ReaderUtilities {

	public static void read(Reader reader, ReaderRunnable runnable) {
		try {
			LineNumberReader lineNumberReader = new LineNumberReader(reader);
			int line = 0;
			while (true) {
				String text = lineNumberReader.readLine();
				if (text == null) break;
				runnable.readLine(line, text);
				line++;
			}

		} catch (IOException e) {
			AppLog.e(e);
		}
	}

	public interface ReaderRunnable {
		boolean readLine(int line, String text);

		void readLineBreak(String lineBreak);
	}
}
