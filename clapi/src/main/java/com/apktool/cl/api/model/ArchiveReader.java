package com.apktool.cl.api.model;

import java.io.IOException;
import java.io.Reader;

public interface ArchiveReader {
    long getArchiveVersion(String archivePath);

    String[] getArchiveEntries(String archivePath) throws IOException;

    Reader getArchiveEntryReader(String archivePath, String entryName, String encoding) throws IOException;

    void close();
}
