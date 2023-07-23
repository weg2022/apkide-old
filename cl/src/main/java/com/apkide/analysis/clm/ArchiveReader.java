package com.apkide.analysis.clm;

import java.io.IOException;
import java.io.Reader;

public interface ArchiveReader {
   long getArchiveVersion(String filePath);

   String[] getArchiveEntries(String filePath) throws IOException;

   Reader getArchiveEntryReader(String filePath, String entryName, String encoding) throws IOException;

   void close();
}
