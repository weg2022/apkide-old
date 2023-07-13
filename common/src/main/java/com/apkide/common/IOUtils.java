package com.apkide.common;

import static java.security.MessageDigest.getInstance;

import androidx.annotation.NonNull;

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
	private static final int BUFFER_SIZE = 4096;
	public static boolean equals(InputStream stream1, InputStream stream2) throws IOException {
		try (stream1; stream2) {
			byte[] buf1 = new byte[8192];
			byte[] buf2 = new byte[8192];
			while (true) {
				int i1 = readMax(stream1, buf1);
				int i2 = readMax(stream2, buf2);
				if (i1 != i2) {
					return false;
				}
				if (i1 == -1) {
					return true;
				}
				for (int i = 0; i < i1; i++) {
					if (buf1[i] != buf2[i]) {
						return false;
					}
				}
			}
		}
	}

	public static int readMax(InputStream inStream, byte[] arr) throws IOException {
		return readMax(inStream, arr, 0, arr.length);
	}

	public static int readMax(InputStream inStream, byte[] arr, int off, int len) throws IOException {
		int count = 0;
		while (count < len) {
			int currCount = inStream.read(arr, off + count, len - count);
			if (currCount == -1) {
				if (count > 0) {
					return count;
				}
				return -1;
			}
			count += currCount;
		}
		return count;
	}

	@NonNull
	public static byte[] readBytes(@NonNull InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copyBytes(in, out);
		return out.toByteArray();
	}
	
	@NonNull
	public static byte[] readBytes(@NonNull InputStream in, boolean autoClose) throws IOException {
		byte[] result = readBytes(in);
		if (autoClose)
			safeClose(in);
		return result;
	}
	
	@NonNull
	public static String readString(@NonNull Reader reader) throws IOException {
		char[] buffer = new char[BUFFER_SIZE / 2];
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
	public static String readString(@NonNull Reader reader, boolean autoClose) throws IOException {
		String result = readString(reader);
		if (autoClose)
			safeClose(reader);
		return result;
	}
	
	public static void writeString(@NonNull Writer writer, @NonNull CharSequence text, boolean autoClose) throws IOException {
		writer.append(text);
		if (autoClose)
			safeClose(writer);
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
			if (read == -1) {
				break;
			}
			out.write(buffer, 0, read);
			byteCount += read;
		}
		return byteCount;
	}
	
	@NonNull
	public static LineIterator lineIterator(@NonNull Reader reader) {
		return new LineIterator(reader);
	}
	
	private static void internalClose(Closeable closeable) {
		if (closeable==null)return;
		try {
			closeable.close();
		} catch (IOException e) {
			AppLog.e(e);
		}
	}

	public static void safeClose(Closeable... closeable) {
		if (closeable==null)return;
		for (Closeable c : closeable) {
			internalClose(c);
		}
	}
	
}
