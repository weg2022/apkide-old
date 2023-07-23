package com.apkide.analysis.cle;

import java.io.IOException;
import java.io.Reader;

public interface BinaryArchiveSupport {
   String[] getSupportedArchiveFileExtensions();

   boolean supportsClassfileArchives();

   Reader getSignatureFileContent(String filePath, String entryName) throws IOException;

   String[] getSignatureFiles(String filePath) throws IOException;

   Reader getMetadataXml(String filePath) throws IOException;
}
