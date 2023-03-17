package com.apkide.language.impl.classfile;

import static java.io.File.separator;

import com.apkide.common.AppLog;
import com.apkide.common.FileUtils;
import com.apkide.common.IOUtils;

import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.jd.core.v1.api.loader.Loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import brut.util.AssetsProvider;

public class ClassFilePreProcessor {
	private ZipFile androidJarFile;//Need to destroy ?
	private boolean archiveOpened = false;
	private String filePath;
	private ZipFile archiveFile;
	private final HashMap<String, ZipFile> cachedArchives = new HashMap<>();
	private final HashMap<String, byte[]> cachedFiles = new HashMap<>();
	private final HashMap<String, Boolean> cachedFilesLoaded = new HashMap<>();
	private static final ThreadLocal<ClassFileToJavaSourceDecompiler> DECOMPILER_TL = ThreadLocal
			.withInitial(ClassFileToJavaSourceDecompiler::new);

	private final StringBuilderPrinter printer = new StringBuilderPrinter();
	private final Loader loader = new Loader() {
		@Override
		public boolean canLoad(String internalName) {
			int index = internalName.lastIndexOf(".class");
			if (index < 0)
				internalName += ".class";

			if (archiveOpened) {
				if (cachedFilesLoaded.containsKey(archiveFile.getName() + "/" + internalName))
					return Boolean.TRUE.equals(cachedFilesLoaded.get(archiveFile.getName() + "/" + internalName));

				ZipEntry entry = archiveFile.getEntry(internalName);
				if (entry == null)
					entry = archiveFile.getEntry("src/" + internalName);
				if (entry == null)
					entry = archiveFile.getEntry("src\\" + internalName);
				if (entry != null) {
					cachedFilesLoaded.put(archiveFile.getName() + "/" + internalName, true);
					return true;
				}
			}
			if (cachedFilesLoaded.containsKey(androidJarFile.getName() + "/" + internalName)) {
				return Boolean.TRUE.equals(cachedFilesLoaded.get(androidJarFile.getName() + "/" + internalName));
			}

			if (androidJarFile.getEntry(internalName) != null) {
				cachedFilesLoaded.put(androidJarFile.getName(), true);
				return true;
			}

			if (cachedFilesLoaded.containsKey(internalName))
				return Boolean.TRUE.equals(cachedFilesLoaded.get(internalName));

			File file = new File(internalName);
			if (file.exists()) {
				cachedFilesLoaded.put(file.getAbsolutePath(), true);
				return true;
			}
			return false;
		}

		@Override
		public byte[] load(String internalName) {
			try {
				int index = internalName.lastIndexOf(".class");
				if (index < 0)
					internalName += ".class";

				if (archiveOpened) {
					if (cachedFiles.containsKey(archiveFile.getName() + "/" + internalName)) {
						return cachedFiles.get(archiveFile.getName() + "/" + internalName);
					}
					ZipEntry entry = archiveFile.getEntry(internalName);
					if (entry == null)
						entry = archiveFile.getEntry("src/" + internalName);
					if (entry == null)
						entry = archiveFile.getEntry("src\\" + internalName);
					if (entry != null)
						return IOUtils.readBytes(archiveFile.getInputStream(entry), true);
				}

				if (androidJarFile.getEntry(internalName) != null) {
					if (cachedFiles.containsKey(androidJarFile.getName() + "/" + internalName)) {
						return cachedFiles.get(androidJarFile.getName() + "/" + internalName);
					}
					byte[] bytes = IOUtils.readBytes(androidJarFile.getInputStream(androidJarFile.getEntry(internalName)), true);
					cachedFiles.put(androidJarFile.getName() + "/" + internalName, bytes);
					return bytes;
				}

				if (filePath != null)
					return IOUtils.readBytes(new FileInputStream(filePath), true);
			} catch (IOException e) {
				AppLog.e(e);
			}
			return new byte[0];
		}
	};

	public ClassFilePreProcessor() {
		try {
			androidJarFile = new ZipFile(AssetsProvider.get().foundFile("android.jar").getAbsolutePath());
		} catch (IOException e) {
			AppLog.e(e);
		}
	}

	private void openArchive(String archivePath) throws IOException {
		archiveOpened = true;
		archiveFile = new ZipFile(archivePath);
	}

	public void closeArchive() throws IOException {
		IOUtils.safeClose(archiveFile);
		archiveFile = null;
		archiveOpened = false;
		cachedArchives.clear();
		cachedFiles.clear();
		cachedArchives.clear();
	}

	public Reader getArchiveEntryReader(String archivePath, String entryPath, String encoding) throws IOException {

		if (new File(archivePath).isDirectory()) {
			if (entryPath.endsWith(".class")) {
				printer.init();
				printer.setEscapeUnicode(true);
				this.filePath = archivePath + File.separator + entryPath;
				try {
					DECOMPILER_TL.get().decompile(loader, printer, FileUtils.getFileName(entryPath));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				return new StringReader(printer.toString());
			}
			FileInputStream inputStream = new FileInputStream(archivePath + separator + entryPath);
			return encoding == null ? new InputStreamReader(inputStream) : new InputStreamReader(inputStream, encoding);
		} else {
			if (entryPath.endsWith(".class")) {
				if (!archiveOpened)
					openArchive(archivePath);
				printer.init();
				printer.setEscapeUnicode(false);
				this.archiveFile = new ZipFile(archivePath);
				try {
					DECOMPILER_TL.get().decompile(loader, printer, entryPath);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				return new StringReader(printer.toString());
			}
		}

		if (!archiveOpened)
			openArchive(archivePath);
		ZipEntry entry = archiveFile.getEntry(entryPath);
		if (entry == null) entry = archiveFile.getEntry("src/" + entryPath);
		if (entry == null) entry = archiveFile.getEntry("src\\" + entryPath);
		InputStreamWrapper input = new InputStreamWrapper(archiveFile.getInputStream(entry), entry.getSize());
		return encoding == null ? new InputStreamReader(input) : new InputStreamReader(input, encoding);
	}


	public boolean isArchiveFileEntry(String path) {
		return path.endsWith(".class") || path.endsWith(".java");
	}

	public List<String> getArchiveDirectoryEntries(String archivePath, String entryPath) throws IOException {
		ArrayList<String> entities = new ArrayList<>();
		Enumeration<? extends ZipEntry> entries;
		if (cachedArchives.get(archivePath) == null) {
			ZipFile zipFile = new ZipFile(archivePath);
			cachedArchives.put(archivePath, zipFile);
		}

		entries = cachedArchives.get(archivePath).entries();
		while (entries.hasMoreElements()) {
			ZipEntry nextElement = entries.nextElement();
			String name = nextElement.getName();
			if (name.endsWith("/"))
				name = name.substring(0, name.length() - 1);

			if (name.startsWith(entryPath) && !name.equals(entryPath) && name.indexOf("/", entryPath.length() + 1) == -1) {
				if (nextElement.isDirectory()) {
					entities.add(archivePath + '/' + name);
				} else if (!nextElement.isDirectory() && name.lastIndexOf("$") == -1 && name.endsWith(".class")) {
					entities.add(archivePath + '/' + name);
				} else if (!nextElement.isDirectory() && name.endsWith(".java")) {
					if (name.startsWith("src/") || name.startsWith("src\\"))
						name = name.substring(4);

					entities.add(archivePath + '/' + name);
				}
			}
		}
		closeArchive();
		return entities;
	}


	private static class InputStreamWrapper extends InputStream {
		private InputStream in;
		private final long size;
		private long readLength;

		public InputStreamWrapper(InputStream in, long size) {
			this.in = in;
			this.size = size;
			this.readLength = 0;
		}

		@Override
		public int read() throws IOException {
			byte[] bytes = new byte[1];
			if (read(bytes) == -1) return -1;
			return bytes[0];
		}

		@Override
		public int read(byte[] b) throws IOException {
			return read(b, 0, b.length);
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			int read;
			if (readLength < size && (read = in.read(b, off, len)) != -1) {
				readLength += read;
				return read;
			}
			return -1;
		}

		@Override
		public void close() throws IOException {
			IOUtils.safeClose(in);
			in = null;
		}
	}
}
