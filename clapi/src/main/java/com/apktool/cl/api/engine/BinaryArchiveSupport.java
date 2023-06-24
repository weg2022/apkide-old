package com.apktool.cl.api.engine;

import java.io.IOException;
import java.io.Reader;

public interface BinaryArchiveSupport {
    String[] getSupportedArchiveFileExtensions();

    boolean supportsClassfileArchives();

    boolean supportsDllArchives();

    Reader getSignatureFileContent(String archivePath, String entryPath) throws IOException;

    String[] getSignatureFiles(String path) throws IOException;

    Reader getMetadataXml(String path) throws IOException;
}
