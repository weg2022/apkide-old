package com.apkide.ui.filesystem;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public interface FileArchiveReader {



    Reader getArchiveEntryReader(String archivePath, String entryName, String encoding) throws IOException;

    List<String> getArchiveDirectoryEntries(String archivePath, String entryName) throws IOException;

    boolean isArchiveFileEntry(String filePath);

    long getArchiveVersion(String archivePath);

    void closeArchive() throws IOException;

}
