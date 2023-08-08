package com.apkide.common.io;

import static java.security.MessageDigest.getInstance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.common.LineIterator;
import com.apkide.common.app.AppLog;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class IOUtils {
	private static final int BUFFER_SIZE = 8192;

	@NonNull
	public static byte[] readBytes(@NonNull InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copyBytes(in, out);
		return out.toByteArray();
	}

	@NonNull
	public static byte[] readBytes(@NonNull InputStream in, boolean autoClose) throws IOException {
		byte[] result = readBytes(in);
		if (autoClose) safeClose(in);
		return result;
	}

	@NonNull
	public static String readString(@NonNull Reader reader) throws IOException {
		char[] buffer = new char[BUFFER_SIZE / 2];
		StringBuilder builder = new StringBuilder();
		while (true) {
			int read = reader.read(buffer);
			if (read == -1) break;
			builder.append(buffer, 0, read);
		}
		return builder.toString();
	}

	@NonNull
	public static String readString(@NonNull Reader reader, boolean autoClose) throws IOException {
		String result = readString(reader);
		if (autoClose) safeClose(reader);
		return result;
	}

	public static void writeString(@NonNull Writer writer, @NonNull CharSequence text, boolean autoClose) throws IOException {
		writer.append(text);
		if (autoClose) safeClose(writer);
	}

	@NonNull
	public static byte[] toDigest(@NonNull InputStream in, @NonNull String algorithm) throws IOException {
		MessageDigest digester;
		try {
			digester = getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			AppLog.e(e);
			return new byte[0];
		}

		byte[] buffer = new byte[BUFFER_SIZE];
		int len;
		while ((len = in.read(buffer)) != -1) {
			digester.update(buffer, 0, len);
		}
		return digester.digest();
	}

	public static int copyBytes(@NonNull InputStream in, @NonNull OutputStream out) throws IOException {
		int byteCount = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		while (true) {
			int read = in.read(buffer);
			if (read == -1) break;

			out.write(buffer, 0, read);
			byteCount += read;
		}
		return byteCount;
	}

	@NonNull
	public static LineIterator lineIterator(@NonNull Reader reader) {
		return new LineIterator(reader);
	}

	public static void safeClose(@Nullable Closeable... closeable) {
		if (closeable == null) return;
		for (Closeable c : closeable) {
			try {
				if (c != null) c.close();
			} catch (IOException e) {
				AppLog.e(e);
			}
		}
	}

}
