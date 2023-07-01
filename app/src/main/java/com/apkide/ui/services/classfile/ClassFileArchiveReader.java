package com.apkide.ui.services.classfile;

import static com.apkide.ui.services.classfile.ClassDecompiler.decompileFromArchive;
import static com.apkide.ui.services.classfile.ClassDecompiler.decompileFromDir;
import static java.io.File.separator;

import com.apkide.common.AppLog;
import com.apkide.common.InputStreamWrapper;
import com.apkide.ui.services.FileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ClassFileArchiveReader implements FileSystem.FileArchiveReader {
    private ZipFile archiveFile;

    @Override
    public Reader getArchiveEntryReader(String archivePath, String entryName, String encoding) throws IOException {
        if (new File(archivePath).isDirectory()) {
            if (entryName.endsWith(".class")) {
                AppLog.s(archivePath+": "+entryName);
                return new StringReader(decompileFromDir(archivePath, entryName));
            }
            FileInputStream inputStream = new FileInputStream(archivePath + separator + entryName);
            return encoding == null ? new InputStreamReader(inputStream) : new InputStreamReader(inputStream, encoding);
        } else {
            if (entryName.endsWith(".class")) {
                if (archiveFile == null)
                    archiveFile = new ZipFile(archivePath);

                return new StringReader(decompileFromArchive(archiveFile, archivePath, entryName));
            }
        }

        if (archiveFile == null)
            archiveFile = new ZipFile(archivePath);

        ZipEntry entry = archiveFile.getEntry(entryName);
        if (entry == null) entry = archiveFile.getEntry("src/" + entryName);
        if (entry == null) entry = archiveFile.getEntry("src\\" + entryName);
        InputStreamWrapper input = new InputStreamWrapper(archiveFile.getInputStream(entry), entry.getSize());
        return encoding == null ? new InputStreamReader(input) : new InputStreamReader(input, encoding);
    }

    @Override
    public List<String> getArchiveDirectoryEntries(String archivePath, String entryName) throws IOException {
        ArrayList<String> entities = new ArrayList<>();
        Enumeration<? extends ZipEntry> entries;
        ZipFile zipFile = new ZipFile(archivePath);

        entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry nextElement = entries.nextElement();
            String name = nextElement.getName();
            if (name.endsWith("/"))
                name = name.substring(0, name.length() - 1);

            if (name.startsWith(entryName) && !name.equals(entryName) && name.indexOf("/", entryName.length() + 1) == -1) {
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

    @Override
    public boolean isArchiveFileEntry(String filePath) {
        return filePath.endsWith(".class") || filePath.endsWith(".java");
    }

    @Override
    public long getArchiveVersion(String archivePath) {
        return 0;
    }

    @Override
    public void closeArchive() throws IOException {
        if (archiveFile != null) {
            archiveFile.close();
        }
    }
}
