package com.apkide.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.common.io.unicode.UnicodeReader;
import com.apkide.common.io.unicode.UnicodeWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class NormalizedReadWriter {
    
    private static final Object sLock = new Object();
    
    private static NormalizedReadWriter sReader = new NormalizedReadWriter();
    
    public static void set(@NonNull NormalizedReadWriter reader) {
        synchronized (sLock) {
            sReader = reader;
        }
    }
    
    @NonNull
    public static NormalizedReadWriter get() {
        synchronized (sLock) {
            return sReader;
        }
    }
    
    
    @NonNull
    public Reader createReader(@NonNull Reader reader) throws IOException {
		return new Reader() {
			boolean inCRLF;
			
			@Override
			public int read(char[] cbuf, int off, int len) throws IOException {
				int readLen = reader.read(cbuf, off, len);
				if (readLen == -1) {
					return -1;
				} else {
					boolean skipCR = inCRLF;
					int destOff = off;
					int srcOff = off;
					int maxOff = off + readLen;
					
					while (srcOff < maxOff) {
						char c = cbuf[srcOff++];
						switch (c) {
							case '\n':
								if (!skipCR)
									cbuf[destOff++] = '\n';
								
								skipCR = false;
								break;
							case '\r':
								cbuf[destOff++] = '\n';
								skipCR = true;
								break;
							default:
								cbuf[destOff++] = c;
								skipCR = false;
						}
					}
					
					inCRLF = skipCR;
					return destOff - off;
				}
			}
			
			@Override
			public void close() throws IOException {
				reader.close();
			}
		};
	}
    
    @NonNull
    public Reader createBomReader(@NonNull InputStream input, @Nullable String encoding) throws IOException {
        return new UnicodeReader(input, encoding);
    }
    
    public Writer createBomWriter(@NonNull OutputStream output, @NonNull String encoding) throws IOException {
        return new UnicodeWriter(output, encoding);
    }
    
}
