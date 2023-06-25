package com.apkide.common.log;

import java.io.IOException;
import java.io.OutputStream;

public class LogOutputStream extends OutputStream {

    public static final int LEVEL_WARNING = 2;
    public static final int LEVEL_ERROR = 1;
    public static final int LEVEL_INFO = 0;
    private static final byte LineFeed = '\n';
    private final StringBuffer msg = new StringBuffer();
    private final String name;
    private final int level;

    public LogOutputStream(String name, int level) {
        this.name = name;
        this.level = level;
    }

    @Override
    public void write(int b) throws IOException {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) b;
        msg.append(new String(bytes));
        if (bytes[0] == LineFeed) {
            msg.deleteCharAt(msg.length());
            flush();
        }
    }

    @Override
    public void flush() throws IOException {
        switch (level) {
            case LEVEL_ERROR:
                Logger.get(name).error(msg.toString());
                break;
            case LEVEL_WARNING:
                Logger.get(name).warning(msg.toString());
                break;
            case LEVEL_INFO:
                Logger.get(name).info(msg.toString());
                break;
        }
        msg.setLength(0);
    }
}
