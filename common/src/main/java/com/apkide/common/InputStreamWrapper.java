package com.apkide.common;


import java.io.IOException;
import java.io.InputStream;

public class InputStreamWrapper extends InputStream {
    private InputStream in;
    private final long size;
    private long readLength;

    public InputStreamWrapper(InputStream in, long size) {
        this.in = in;
        this.size = size;
        this.readLength = 0;
    }

    @Override
    public int read() throws IOException {
        byte[] bytes = new byte[1];
        if (read(bytes) == -1) return -1;
        return bytes[0];
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read;
        if (readLength < size && (read = in.read(b, off, len)) != -1) {
            readLength += read;
            return read;
        }
        return -1;
    }

    @Override
    public void close() throws IOException {
        if (in != null) {
            in.close();
            in = null;
        }
    }
}
