package com.apkide.language.impl.classfile;

import com.apkide.common.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ClassFilePreProcessor {

	private boolean archiveOpened = false;
	private final Hashtable<String, ZipFile> archiveMap = new Hashtable<>();
	private final Hashtable<ZipFile, List<ZipEntry>> entriesMap = new Hashtable<>();

	public ClassFilePreProcessor() {
	}

	private void openArchive() {
		archiveOpened = true;
	}

	public void closeArchive() throws IOException {
		if (archiveOpened) {
			for (Map.Entry<String, ZipFile> entry : archiveMap.entrySet()) {
				try {
					entry.getValue().close();
				} catch (IOException ignored) {

				}
			}
		}
		archiveMap.clear();
		entriesMap.clear();
		archiveOpened = false;
	}

	public Reader getArchiveEntryReader(String archivePath, String entryPath, String encoding) throws IOException {
		if (!archiveOpened)
			openArchive();

		if (archivePath.toUpperCase().endsWith(".CLASS")) {
			//TODO
			return null;
		} else if (new File(archivePath).isDirectory()) {
			if (entryPath.endsWith(".java")) {
				FileInputStream inputStream = new FileInputStream(archivePath + File.separator + entryPath);
				return encoding == null ? new InputStreamReader(inputStream) : new InputStreamReader(inputStream, encoding);
			}
			//TODO
			return null;
		} else if (entryPath.endsWith(".class")) {
			//TODO
			return null;
		} else {
			ZipFile zipFile = getArchive(archivePath);
			ZipEntry entry = zipFile.getEntry(entryPath);
			if (entry == null)
				entry = zipFile.getEntry("src/" + entryPath);

			if (entry == null)
				entry = zipFile.getEntry("src\\" + entryPath);
			InputStreamWrapper input = new InputStreamWrapper(zipFile.getInputStream(entry), entry.getSize());
			return encoding == null ? new InputStreamReader(input) : new InputStreamReader(input, encoding);
		}
	}


	private ZipFile getArchive(String archivePath) throws IOException {
		if (archivePath.endsWith(File.separator))
			archivePath = archivePath.substring(0, archivePath.length() - 1);

		if (archiveMap.containsKey(archivePath))
			return archiveMap.get(archivePath);

		ZipFile zipFile = new ZipFile(archivePath);
		archiveMap.put(archivePath, zipFile);
		return zipFile;
	}

	private List<ZipEntry> getEntries(ZipFile zipFile) {
		if (entriesMap.containsKey(zipFile)) {
			return entriesMap.get(zipFile);
		}
		ArrayList<ZipEntry> arrayList = new ArrayList<>();
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry nextElement = entries.nextElement();
			if (!nextElement.isDirectory() && nextElement.getName().lastIndexOf(36) != -1) {
				arrayList.add(nextElement);
			}
		}
		this.entriesMap.put(zipFile, arrayList);
		return arrayList;
	}


	public boolean isArchiveFileEntry(String path) {
		return path.endsWith(".class") || path.endsWith(".java");
	}

	public List<String> getArchiveDirectoryEntries(String archivePath, String entryPath) throws IOException {
		ArrayList<String> entities = new ArrayList<>();
		Enumeration<? extends ZipEntry> entries = getArchive(archivePath).entries();
		while (entries.hasMoreElements()) {
			ZipEntry nextElement = entries.nextElement();
			String name = nextElement.getName();
			if (name.endsWith("/")) {
				name = name.substring(0, name.length() - 1);
			}
			if (name.startsWith(entryPath) && !name.equals(entryPath) && name.indexOf(47, entryPath.length() + 1) == -1) {
				if (nextElement.isDirectory()) {
					entities.add(archivePath + '/' + name);
				} else if (!nextElement.isDirectory() && name.lastIndexOf(36) == -1 && name.endsWith(".class")) {
					entities.add(archivePath + '/' + name);
				} else if (!nextElement.isDirectory() && name.endsWith(".java")) {
					if (name.startsWith("src/") || name.startsWith("src\\")) {
						name = name.substring(4);
					}
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
			if (read(bytes) == -1)
				return -1;
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
