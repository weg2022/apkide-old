package com.apkide.codemodel.api.language;

import java.io.IOException;
import java.io.Reader;

public interface ArchiveReader {
	long getArchiveVersion(String path);
	
	String[] getArchiveEntries(String path) throws IOException;
	
	Reader getArchiveEntryReader(String filePath, String entryPath, String encoding) throws IOException;
	
	void close();
}
