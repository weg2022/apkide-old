package com.apkide.analysis.api.clm.callback;

import java.io.InputStream;
import java.io.Reader;

public interface FileReaderCallback {
    Reader createBomReader(InputStream inStream, String fallbackEncoding);
}
