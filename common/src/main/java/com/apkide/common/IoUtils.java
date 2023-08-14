package com.apkide.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.zip.Checksum;

public class IoUtils {
	private static final int BUFFER_SIZE = 8192;

	@NonNull
	public static LineIterator lineIterator(@NonNull Reader reader) {
		return new LineIterator(reader);
	}

	@NonNull
	public static byte[] readAllBytes(@NonNull InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copyAllBytes(in, out);
		return out.toByteArray();
	}

	@NonNull
	public static byte[] readAllBytesAndClose(@NonNull InputStream in) throws IOException {
		try {
			return readAllBytes(in);
		} finally {
			safeClose(in);
		}
	}

	@NonNull
	public static String readAllChars(@NonNull Reader reader) throws IOException {
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
	public static String readAllCharsAndClose(@NonNull Reader reader) throws IOException {
		try {
			return readAllChars(reader);
		} finally {
			safeClose(reader);
		}
	}

	public static void writeAllCharsAndClose(@NonNull Writer writer,@NonNull CharSequence text) throws IOException {
		try {
			writer.append(text);
		} finally {
			safeClose(writer);
		}
	}

	public static void updateChecksum(@NonNull InputStream in,@NonNull Checksum checksum) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		while (true) {
			int read = in.read(buffer);
			if (read == -1) {
				break;
			}
			checksum.update(buffer, 0, read);
		}
	}


	public static int copyAllBytes(@NonNull InputStream in,@NonNull OutputStream out) throws IOException {
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
				try {
					close.close();
				} catch (IOException e) {
					AppLog.e(e);
				}
			}
		}
	}

}
