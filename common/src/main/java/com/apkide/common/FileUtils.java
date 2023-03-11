package com.apkide.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;

@SuppressWarnings("unchecked")
public final class FileUtils {
	
	@NonNull
	public static byte[] readBytes(@NonNull File file) throws IOException {
		return readBytes(file, true);
	}
	
	@NonNull
	public static byte[] readBytes(@NonNull File file, boolean autoClose) throws IOException {
		return IOUtils.readBytes(Files.newInputStream(file.toPath()), autoClose);
	}
	
	public static void writeBytes(@NonNull File file, @NonNull byte[] content) throws IOException {
		writeBytes(file, content, true);
	}
	
	public static void writeBytes(@NonNull File file, @NonNull byte[] content, boolean autoClose) throws IOException {
		OutputStream out = Files.newOutputStream(file.toPath());
		out.write(content);
		if (autoClose)
			IOUtils.safeClose(out);
	}
	
	@NonNull
	public static String readString(File file) throws IOException {
		return readString(file, "UTF-8", true);
	}
	
	@NonNull
	public static String readString(@NonNull File file, boolean autoClose) throws IOException {
		return readString(file, "UTF-8", autoClose);
	}
	
	@NonNull
	public static String readString(@NonNull File file, @NonNull Charset charset, boolean autoClose) throws IOException {
		Reader reader = new InputStreamReader(Files.newInputStream(file.toPath()), charset);
		return IOUtils.readString(reader, autoClose);
	}
	
	@NonNull
	public static String readString(@NonNull File file, @NonNull String charset, boolean autoClose) throws IOException {
		Reader reader = new InputStreamReader(Files.newInputStream(file.toPath()), charset);
		return IOUtils.readString(reader, autoClose);
	}
	
	public static void writeString(@NonNull File file, @NonNull CharSequence text) throws IOException {
		writeString(file, "UTF-8", text, false, true);
	}
	
	
	public static void writeString(@NonNull File file, @NonNull CharSequence text, boolean autoClose) throws IOException {
		writeString(file, "UTF-8", text, false, autoClose);
	}
	
	
	public static void appendString(@NonNull File file, @NonNull CharSequence text) throws IOException {
		writeString(file, "UTF-8", text, true, true);
	}
	
	public static void appendString(@NonNull File file, @NonNull CharSequence text, boolean autoClose) throws IOException {
		writeString(file, "UTF-8", text, true, autoClose);
	}
	
	public static void writeString(@NonNull File file, @NonNull Charset charset, @NonNull CharSequence text, boolean append) throws IOException {
		writeString(file, charset, text, append, true);
	}
	
	public static void writeString(@NonNull File file, @NonNull String charset, @NonNull CharSequence text, boolean append) throws IOException {
		writeString(file, charset, text, append, true);
	}
	
	public static void writeString(@NonNull File file, @NonNull Charset charset, @NonNull CharSequence text, boolean append, boolean autoClose) throws IOException {
		Writer writer = new OutputStreamWriter(new FileOutputStream(file, append), charset);
		IOUtils.writeString(writer, text, autoClose);
	}
	
	public static void writeString(@NonNull File file, @NonNull String charset, @NonNull CharSequence text, boolean append, boolean autoClose) throws IOException {
		Writer writer = new OutputStreamWriter(new FileOutputStream(file, append), charset);
		IOUtils.writeString(writer, text, autoClose);
	}
	
	public static void copyFile(@NonNull File sourceFile, @NonNull File destFile) throws IOException {
		copyFile(sourceFile, destFile,true);
	}
	
	public static void copyFile(@NonNull File sourceFile, @NonNull File destFile, boolean autoClose) throws IOException {
		InputStream in = new BufferedInputStream(Files.newInputStream(sourceFile.toPath()));
		OutputStream out = new BufferedOutputStream(Files.newOutputStream(destFile.toPath()));
		IOUtils.copyBytes(in, out);
		if (autoClose) {
			IOUtils.safeClose(out);
			IOUtils.safeClose(in);
		}
	}
	
	public static void copyFile(@NonNull String sourcePath, @NonNull String destPath) throws IOException {
		copyFile(sourcePath,destPath,true);
	}
	
	public static void copyFile(@NonNull String sourcePath, @NonNull String destPath,boolean autoClose) throws IOException {
		copyFile(new File(sourcePath), new File(destPath),autoClose);
	}
	
	@NonNull
	public static <T> T readObject(@NonNull File file) throws IOException, ClassNotFoundException {
		return readObject(file, true);
	}
	
	@NonNull
	public static <T> T readObject(@NonNull File file, boolean autoClose) throws IOException, ClassNotFoundException {
		FileInputStream fileIn = new FileInputStream(file);
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(fileIn));
		T o = (T) in.readObject();
		if (autoClose)
			IOUtils.safeClose(in);
		return o;
	}
	
	public static void writeObject(@NonNull File file, @NonNull Object object) throws IOException {
		writeObject(file, object, true);
	}
	
	public static void writeObject(@NonNull File file, @NonNull Object object, boolean autoClose) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(file);
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(fileOut));
		out.writeObject(object);
		out.flush();
		fileOut.getFD().sync();
		if (autoClose)
			IOUtils.safeClose(out);
	}
	
	public static boolean deleteDir(File dir, boolean failFast) {
		File file = internalFileDelete(dir, failFast);
		if (failFast) {
			if (file != null)
				AppLog.e("Could not delete file: " + file.getPath());
			return file == null;
		} else
			return !dir.exists();
	}
	
	public static boolean deleteDirRecursiveBestEffort(File dir) {
		internalFileDelete(dir, false);
		return !dir.exists();
	}
	
	@Nullable
	private static File internalFileDelete(@NonNull File dir, boolean failFast) {
		if (!dir.exists())
			return null;
		
		File[] files = dir.listFiles();
		if (files == null)
			return null;
		
		for (File file : files) {
			if (file.isDirectory()) {
				File failed = internalFileDelete(file, failFast);
				if (failFast && failed != null)
					return failed;
			}
			if (!file.delete()) {
				if (failFast && file.exists())
					return file;
			}
		}
		if (!dir.delete()) {
			if (failFast && dir.exists())
				return dir;
		}
		return null;
	}
}
