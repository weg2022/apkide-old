package com.apktool.cl.api.engine;

import java.io.IOException;
import java.io.Reader;

public interface BinaryArchiveSupport {
    String[] getSupportedArchiveFileExtensions();

    boolean supportsClassfileArchives();

    boolean supportsDllArchives();

    Reader getSignatureFileContent(String archivePath, String entryName) throws IOException;

    String[] getSignatureFiles(String archivePath) throws IOException;

    Reader getMetadataXml(String path) throws IOException;
}
