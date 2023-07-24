package com.apkide.ui.util;


import static java.io.File.separator;

import com.apkide.common.IOUtils;

import java.io.ByteArrayInputStream;
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


public class JarArchiveReader  {
    private ZipFile archiveFile;

    public Reader getArchiveEntryReader(String archivePath, String entryName, String encoding) throws IOException {

        if (new File(archivePath).isDirectory()) {
            if (entryName.endsWith(".class")) {
                return new StringReader(ClassDecompiler.decompileFromDir(archivePath, entryName));
            }
            FileInputStream inputStream = new FileInputStream(archivePath + separator + entryName);
            return encoding == null ? new InputStreamReader(inputStream) : new InputStreamReader(inputStream, encoding);
        } else {
            if (entryName.endsWith(".class")) {
                if (archiveFile == null)
                    archiveFile = new ZipFile(archivePath);
                return new StringReader(ClassDecompiler.decompileFromArchive(archiveFile, archivePath, entryName));
            }
        }


        if (archiveFile == null)
            archiveFile = new ZipFile(archivePath);

        ZipEntry entry = archiveFile.getEntry(entryName);
        if (entry == null) entry = archiveFile.getEntry("src/" + entryName);
        if (entry == null) entry = archiveFile.getEntry("src\\" + entryName);

        byte[] bytes = IOUtils.readBytes(archiveFile.getInputStream(entry));
        return encoding == null ? new InputStreamReader(new ByteArrayInputStream(bytes)) : new InputStreamReader(new ByteArrayInputStream(bytes), encoding);
    }

    public List<String> getArchiveDirectoryEntries(String archivePath, String entryName) throws IOException {
        ArrayList<String> entities = new ArrayList<>();
        Enumeration<? extends ZipEntry> entries;
        ZipFile zipFile = new ZipFile(archivePath);

        entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry next = entries.nextElement();
            String name = next.getName();
            if (name.endsWith("/"))
                name = name.substring(0, name.length() - 1);

            if (name.startsWith(entryName) && !name.equals(entryName) && name.indexOf("/", entryName.length() + 1) == -1) {
                if (next.isDirectory()) {
                    entities.add(archivePath + '/' + name);
                } else {
                    if (name.lastIndexOf("$") == -1 && name.endsWith(".class")) {
                        entities.add(archivePath + '/' + name);
                    } else if (name.endsWith(".java")) {
                        if (name.startsWith("src/") || name.startsWith("src\\"))
                            name = name.substring(4);
                        entities.add(archivePath + '/' + name);
                    } else {
                        //忽略除了class,java以外其它的文件类型
                    }
                }
            }
        }
        zipFile.close();
        return entities;
    }

    public boolean isArchiveFileEntry(String filePath) {
        return filePath.endsWith(".class") ||
                filePath.endsWith(".java");
    }

    public long getArchiveVersion(String archivePath) {
        return 0;
    }


    public void closeArchive() throws IOException {
        if (archiveFile != null) {
            archiveFile.close();
        }
    }

}
