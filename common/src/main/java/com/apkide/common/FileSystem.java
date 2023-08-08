package com.apkide.common;

import android.util.Log;

import androidx.annotation.NonNull;

import com.apkide.common.io.FileUtils;
import com.apkide.common.io.IOUtils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class FileSystem {
	private static final String TAG = "FileSystem";
	private static final int BINARY_CHECK_BYTE_COUNT = 8000;
	private static FileArchiveReader sArchiveReader;
	private static final Object sLock = new Object();

	public static void setArchiveReader(FileArchiveReader archiveReader) {
		synchronized (sLock) {
			sArchiveReader = archiveReader;
		}
	}

	public static FileArchiveReader getArchiveReader() {
		synchronized (sLock) {
			return sArchiveReader;
		}
	}


	public static boolean areEqualTimestamps(long time1, long time2) {
		long difference = Math.abs(time1 - time2);
		if (difference <= 1000)
			return true;
		if (difference % 3600000 <= 0)
			return difference <= 13 * 3600000;
		if ((difference - 1000) % 3600000 == 0)
			return difference <= 13 * 3600000;
		if ((difference + 1000) % 3600000 == 0)
			return difference <= 13 * 3600000;
		return false;
	}

	public static boolean isBinary(String filePath) {
		if (isJarFileEntry(filePath))
			return false;

		long fileLength = new File(filePath).length();
		int readCount = BINARY_CHECK_BYTE_COUNT < fileLength ? BINARY_CHECK_BYTE_COUNT : (int) fileLength;
		try {
			try (FileInputStream fis = new FileInputStream(filePath)) {
				byte[] bytes = new byte[readCount];
				new DataInputStream(fis).readFully(bytes);
				for (byte aByte : bytes) {
					if (aByte == 0)
						return true;
				}
				return false;
			}
		} catch (IOException e) {
			return false;
		}
	}

	public static Reader readFile(String path) throws IOException {
		return readFile(path, null);
	}

	public static Reader readFile(String filePath, String encoding) throws IOException {
		if (isNormalFile(filePath)) {
			if (sArchiveReader != null && sArchiveReader.isArchiveFileEntry(filePath)) {
				Reader reader = sArchiveReader.getArchiveEntryReader(new File(filePath).getParent(), FileUtils.getFileName(filePath), encoding);
				return new Reader() {
					@Override
					public int read(char[] cbuf, int off, int len) throws IOException {
						return reader.read(cbuf, off, len);
					}

					@Override
					public void close() throws IOException {
						sArchiveReader.closeArchive();
					}
				};
			}
			return encoding == null ? new FileReader(filePath) : new InputStreamReader(new FileInputStream(filePath), encoding);
		} else if (isJarFileEntry(filePath))
			return readJarFileEntry(filePath, encoding);
		throw new IOException();
	}

	public static boolean isRegexContainedInFile(String regex, String filePath) {
		try {
			Pattern pattern = Pattern.compile(regex);
			try (Reader r = readFile(filePath)) {
				BufferedReader bufferedReader = new BufferedReader(r);
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					if (pattern.matcher(line).find())
						return true;
				}
				return false;
			}
		} catch (IOException e) {
			return false;
		}
	}

	private static String getEnclosingJar(String path) {
		if (isJar(path))
			return path;
		String parentPath = path;
		while (true) {
			parentPath = getParentDirPath(parentPath);
			if (parentPath == null)
				return null;
			if (isJar(parentPath))
				return parentPath;
		}
	}

	public static boolean isJar(String path) {
		return new File(path).isFile() && (path.endsWith(".jar") || path.endsWith(".zip"));
	}

	private static Reader readJarFileEntry(String filePath, String encoding) throws IOException {
		String jarFilepath = getEnclosingJar(filePath);
		String entryPath = getJarFileEntryRelativePath(filePath);
		if (sArchiveReader == null) {
			throw new IOException("not archive reader.");
		}
		Reader reader = sArchiveReader.getArchiveEntryReader(jarFilepath, entryPath, encoding);
		return new Reader() {
			@Override
			public int read(char[] cbuf, int off, int len) throws IOException {
				return reader.read(cbuf, off, len);
			}

			@Override
			public void close() throws IOException {
				sArchiveReader.closeArchive();
			}
		};
	}

	private static String getJarFileEntryRelativePath(String path) {
		String jarFilepath = getEnclosingJar(path);
		assert jarFilepath != null;
		if (jarFilepath.length() == path.length())
			return "";
		return path.substring(jarFilepath.length() + 1);
	}


	public static void writeFile(String filePath, Reader content) throws IOException {
		FileWriter writer = new FileWriter(filePath, false);
		IOUtils.writeString(writer, IOUtils.readString(content, true), true);
	}

	public static boolean isRoot(String path) {
		return path.equals("/");
	}

	public static String getParentDirPath(String path) {
		if (path.length() == 0)
			return null;
		if (path.equals("/"))
			return null;
		int index = path.lastIndexOf('/');
		if (index == 0)
			return "/";
		else if (index == -1)
			return null;
		return path.substring(0, index);
	}

	public static String getName(String path) {
		return path.substring(path.lastIndexOf('/') + 1);
	}

	public static boolean exists(String path) {
		return new File(path).exists() || isJarFileEntry(path);
	}

	public static boolean isJarFileEntry(String path) {
		String jarPath = getEnclosingJar(path);
		return jarPath != null && sArchiveReader != null && sArchiveReader.isArchiveFileEntry(path);
	}

	public static boolean isJarDirectoryEntry(String path) {
		String jarPath = getEnclosingJar(path);
		return jarPath != null && sArchiveReader != null && !sArchiveReader.isArchiveFileEntry(path);
	}

	public static boolean isJarEntry(String path) {
		String jarPath = getEnclosingJar(path);
		return jarPath != null;
	}

	public static boolean isDirectory(String path) {
		return isNormalDirectory(path) || isJarDirectoryEntry(path);
	}

	public static boolean isNormalDirectory(String path) {
		return new File(path).isDirectory();
	}

	public static boolean isNormalFile(String path) {
		return new File(path).isFile() && !isJar(path);
	}

	public static boolean isFile(String path) {
		return (isNormalFile(path) || isJarFileEntry(path)) && !isJar(path);
	}

	public static List<String> getChildEntries(String dirPath) {
		if (isJarEntry(dirPath)) {
			String jarFilepath = getEnclosingJar(dirPath);
			String entryPath = getJarFileEntryRelativePath(dirPath);
			List<String> entries = null;
			try {
				if (sArchiveReader != null)
					entries = sArchiveReader.getArchiveDirectoryEntries(jarFilepath, entryPath);
			} catch (IOException ignored) {

			}
			if (entries == null)
				return new ArrayList<>();
			return entries;
		} else {
			String[] entries = new File(dirPath).list();
			if (entries == null)
				return new ArrayList<>();
			String[] paths = new String[entries.length];
			String prefix = dirPath.equals("/") ? "/" : dirPath + File.separator;
			for (int i = 0; i < paths.length; i++) {
				paths[i] = prefix + entries[i];
			}
			return Arrays.asList(paths);
		}
	}

	public static String getRelativePath(String parentPath, String filePath) {
		if (parentPath.equals(filePath))
			return "";
		return filePath.substring(parentPath.length() + 1);
	}

	public static boolean isContained(String path, String entryPath) {
		return entryPath != null && (entryPath.equals(path) || entryPath.startsWith(path + "/"));
	}

	public static String getFilePathAfterRename(String filePath, String oldPath, String newPath) {
		return newPath + filePath.substring(oldPath.length());
	}

	public static void rename(String path, String newPath) throws IOException {
		File newFile = new File(newPath);
		if (newFile.exists())
			throw new IOException(newPath + " already exists");
		if (!new File(path).renameTo(newFile))
			throw new IOException(path + " could not be renamed");
	}

	public static void createDir(String path) throws IOException {
		if (!new File(path).mkdir()) {
			if (!new File(path).mkdirs()) {
				throw new IOException(path + " could not be created");
			}
		}
	}

	public static void createFile(String path) throws IOException {
		if (new File(path).exists())
			throw new IOException(path + " already exists");
		new File(path).createNewFile();
	}

	public static void createFile(String path, String content) throws IOException {
		if (new File(path).exists())
			throw new IOException(path + " already exists");
		FileWriter writer = new FileWriter(path);
		writer.write(content);
		writer.close();
	}

	public static void delete(String path) throws IOException {
		delete(new File(path));
	}

	private static void delete(File file) throws IOException {
		if (file.isDirectory()) {
			String[] children = file.list();
			if (children != null) {
				for (String child : children)
					delete(new File(file, child));
			}
		}
		if (!file.delete())
			throw new IOException(file.getPath() + " could not be deleted");
	}

	public static boolean mkdirs(String dirPath) {
		return new File(dirPath).mkdirs();
	}

	public static long getLastModified(String filePath) {
		return new File(filePath).lastModified();
	}

	public static long getLength(String filePath) {
		return new File(filePath).length();
	}

	public static String getExtension(String filePath) {
		String filename = getName(filePath);
		int i = filename.lastIndexOf('.');
		if (i < 0)
			return "";
		return filename.substring(i);
	}

	public static String tryMakeRelative(String rootPath, String path) {
		if (isContained(rootPath, path))
			return path.substring(rootPath.length() + 1);
		else
			return path;
	}

	public static String resolvePath(String rootPath, String relativeOrAbsolutePath) {
		String res = resolvePath0(rootPath, relativeOrAbsolutePath);
		Log.d(TAG, "Resolved (" + rootPath + ", " + relativeOrAbsolutePath + ") to " + res);
		return res;
	}

	private static String resolvePath0(String rootPath, String relativeOrAbsolutePath) {
		String relPath = relativeOrAbsolutePath;
		relPath = relPath.replace("\\\\", "/");
		relPath = relPath.replace("\\", "/");
		if (!relPath.contains("/"))
			return rootPath + "/" + relPath;

		if (relPath.startsWith("/"))
			relPath = ".." + relPath;

		String tryRelative = getCanonicalPathPreservingSymlinks(new File(rootPath, relPath));
		if (new File(tryRelative).exists())
			return tryRelative;
		return relativeOrAbsolutePath;
	}

	public static String getCanonicalPathPreservingSymlinks(File f) {
		List<String> canonicalPathComponents = new ArrayList<>();
		String absolutePath = f.getAbsolutePath();
		String[] absPathComponents = absolutePath.split("[/\\\\]");
		for (String absPathComponent : absPathComponents) {
			if (absPathComponent.length() != 0 && !".".equals(absPathComponent)) {
				if ("..".equals(absPathComponent)) {
					if (canonicalPathComponents.isEmpty()) {
						return absolutePath;
					} else
						canonicalPathComponents.remove(canonicalPathComponents.size() - 1);
				} else
					canonicalPathComponents.add(absPathComponent);
			}
		}
		StringBuilder resBuilder = new StringBuilder();
		for (String canonicalPathComponent : canonicalPathComponents) {
			resBuilder.append("/");
			resBuilder.append(canonicalPathComponent);
		}
		if (absolutePath.endsWith("/")) resBuilder.append("/");
		return resBuilder.toString();
	}

	public static String getEnclosingDir(String path, String dirName) {
		path = getParentDirPath(path);
		while (path != null) {
			if (getName(path).equals(dirName))
				return path;
			path = getParentDirPath(path);
		}
		return null;
	}

	public static boolean isValidFilePath(String path) {
		return path.startsWith("/") && isNormalDirectory(getParentDirPath(path));
	}

	public static int getContainedFileCount(String path, int maxCount, String... suffixes) {
		if (isNormalDirectory(path)) {
			int count = 0;
			List<String> entries;
			entries = getChildEntries(path);
			for (String childPath : entries) {
				count += getContainedFileCount(childPath, maxCount, suffixes);
				if (count >= maxCount)
					return count;
			}
			return count;
		} else if (isNormalFile(path)) {
			for (String suffix : suffixes) {
				if (path.endsWith(suffix))
					return 1;
			}
			return 0;
		} else {
			return 0;
		}
	}

	public static void extractZipFile(InputStream zipIn, String targetPath) throws IOException {
		ZipInputStream zipStream = new ZipInputStream(zipIn);
		ZipEntry entry;
		while ((entry = zipStream.getNextEntry()) != null) {
			String entryPath = entry.getName();
			String entryTargetPath = targetPath + File.separator + entryPath;
			if (entry.isDirectory()) {
				new File(entryTargetPath).mkdirs();
			} else if (!new File(entryTargetPath).isFile()) {
				new File(entryTargetPath).getParentFile().mkdirs();
				FileOutputStream fileOut = new FileOutputStream(entryTargetPath);
				IOUtils.copyBytes(zipStream, fileOut);
				fileOut.close();
			}
		}
	}

	public interface FileArchiveReader {

		@NonNull
		Reader getArchiveEntryReader(String archivePath, String entryName, String encoding) throws IOException;

		@NonNull
		List<String> getArchiveDirectoryEntries(String archivePath, String entryName) throws IOException;

		boolean isArchiveFileEntry(String filePath);

		long getArchiveVersion(String archivePath);

		void closeArchive() throws IOException;
	}
}
