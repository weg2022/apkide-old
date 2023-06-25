package com.apktool.cl.api.model.excpetions;

public class FileNotSynchronizedException extends RuntimeException {
    public FileNotSynchronizedException(String s) {
        super("File is not uptodate " + s);
    }
}
