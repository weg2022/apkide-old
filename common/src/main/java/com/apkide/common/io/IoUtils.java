package com.apkide.common.io;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.common.AppLog;
import com.apkide.common.io.iterator.LineIterator;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class IoUtils {
	private static final int BUFFER_SIZE = 8192;

	@NonNull
	public static LineIterator lineIterator(@NonNull Reader reader) {
		return new LineIterator(reader);
	}

	@NonNull
	public static byte[] readBytes(@NonNull InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copy(in, out);
		return out.toByteArray();
	}

	@NonNull
	public static byte[] readBytesAndClose(@NonNull InputStream in) throws IOException {
		try {
			return readBytes(in);
		} finally {
			safeClose(in);
		}
	}

	@NonNull
	public static String readString(@NonNull Reader reader) throws IOException {
		char[] buffer = new char[BUFFER_SIZE];
		StringBuilder builder = new StringBuilder();
		while (true) {
			int read = reader.read(buffer);
			if (read == -1) {
				break;
			}
			builder.append(buffer, 0, read);
		}
		return builder.toString();
	}

	@NonNull
	public static String readStringAndClose(@NonNull Reader reader) throws IOException {
		try {
			return readString(reader);
		} finally {
			safeClose(reader);
		}
	}

	public static void writeStringAndClose(@NonNull Writer writer, @NonNull CharSequence text) throws IOException {
		try {
			writer.append(text);
		} finally {
			safeClose(writer);
		}
	}
	
	public static int copy(@NonNull InputStream in, @NonNull OutputStream out) throws IOException {
		int byteCount = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		while (true) {
			int read = in.read(buffer);
			if (read == -1) {
				break;
			}
			out.write(buffer, 0, read);
			byteCount += read;
		}
		return byteCount;
	}


	public static void safeClose(@Nullable Closeable... closeable) {
		if (closeable != null) {
			for (Closeable close : closeable) {
				if (close!=null) {
					try {
						close.close();
					} catch (IOException e) {
						AppLog.e(e);
					}
				}
			}
		}
	}

}
