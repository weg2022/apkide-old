package com.apkide.ui.services.file.classfile;

import static java.io.File.separator;

import com.apkide.common.AppLog;
import com.apkide.common.AssetsProvider;
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
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ClassFilePreProcessor {
	private ZipFile androidJarFile;
	private boolean archiveOpened = false;
	private String filePath;
	private ZipFile archiveFile;
	private static final ClassFileToJavaSourceDecompiler DECOMPILER_TL = new ClassFileToJavaSourceDecompiler();
	private final StringBuilderPrinter printer = new StringBuilderPrinter();
	private final Loader loader = new Loader() {
		@Override
		public boolean canLoad(String internalName) {
			int index = internalName.lastIndexOf(".class");
			if (index < 0)
				internalName += ".class";

			if (archiveOpened) {
				ZipEntry entry = archiveFile.getEntry(internalName);
				if (entry == null)
					entry = archiveFile.getEntry("src/" + internalName);
				if (entry == null)
					entry = archiveFile.getEntry("src\\" + internalName);
				if (entry != null)
					return true;
			}

			if (androidJarFile.getEntry(internalName) != null)
				return true;

			File file = new File(internalName);
			return file.exists();
		}

		@Override
		public byte[] load(String internalName) {
			try {
				int index = internalName.lastIndexOf(".class");
				if (index < 0)
					internalName += ".class";

				if (archiveOpened) {
					ZipEntry entry = archiveFile.getEntry(internalName);
					if (entry == null)
						entry = archiveFile.getEntry("src/" + internalName);
					if (entry == null)
						entry = archiveFile.getEntry("src\\" + internalName);
					if (entry != null)
						return IOUtils.readBytes(archiveFile.getInputStream(entry), true);
				}

				if (androidJarFile.getEntry(internalName) != null) {
					return IOUtils.readBytes(androidJarFile.getInputStream(androidJarFile.getEntry(internalName)), true);
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
			androidJarFile = new ZipFile(AssetsProvider.get().foundAndroidFrameworkFile().getAbsolutePath());
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
	}

	public Reader getArchiveEntryReader(String archivePath, String entryPath, String encoding) throws IOException {

		if (new File(archivePath).isDirectory()) {
			if (entryPath.endsWith(".class")) {
				printer.init();
				printer.setEscapeUnicode(true);
				this.filePath = archivePath + File.separator + entryPath;
				try {
					DECOMPILER_TL.decompile(loader, printer, "");
				} catch (Exception e) {
					return new StringReader(e.getMessage());
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
					DECOMPILER_TL.decompile(loader, printer, entryPath);
				} catch (Exception e) {
					return new StringReader(e.getMessage());
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
		ZipFile zipFile = new ZipFile(archivePath);

		entries = zipFile.entries();
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
		zipFile.close();
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
