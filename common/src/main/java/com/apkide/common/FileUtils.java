package com.apkide.common;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.zip.Checksum;

public class FileUtils {
    
    @NonNull
    public static File setExecutable(@NonNull File file) {
        if (file.exists() && !file.canExecute())
            file.setExecutable(true);
        return file;
    }
    
    @NonNull
    public static String getExtension(@NonNull String fileName) {
        int index = fileName.indexOf('.');
        if (index >= 0)
            return fileName.substring(index + 1);
        return "";
    }
    
    
    @NonNull
    public static String getFileName(@NonNull String filePath) {
        int index = filePath.lastIndexOf(File.separator);
        if (index >= 0) {
            return filePath.substring(index + 1);
        }
        return filePath;
    }
    
    @NonNull
    public static LineIterator readLines(@NonNull File file) throws IOException {
        return new LineIterator(new StringReader(readUtf8(file)));
    }
    
    @NonNull
    public static byte[] readBytes(@NonNull File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        return IoUtils.readAllBytesAndClose(is);
    }
    
    
    public static void writeBytes(@NonNull File file, @NonNull byte[] content) throws IOException {
        OutputStream out = new FileOutputStream(file);
        try {
            out.write(content);
        } finally {
            IoUtils.safeClose(out);
        }
    }
    
    @NonNull
    public static String readUtf8(@NonNull File file) throws IOException {
        return readChars(file, "UTF-8");
    }
    
    @NonNull
    public static String readChars(@NonNull File file, @NonNull String charset) throws IOException {
        Reader reader = new InputStreamReader(new FileInputStream(file), charset);
        return IoUtils.readAllCharsAndClose(reader);
    }
    
    public static void writeUtf8(@NonNull File file, @NonNull CharSequence text) throws IOException {
        writeChars(file, "UTF-8", text, false);
    }
    
    public static void appendUtf8(@NonNull File file, @NonNull CharSequence text) throws IOException {
        writeChars(file, "UTF-8", text, true);
    }
    
    public static void writeChars(@NonNull File file, @NonNull String charset, @NonNull CharSequence text, boolean apppend) throws IOException {
        Writer writer = new OutputStreamWriter(new FileOutputStream(file, apppend), charset);
        IoUtils.writeAllCharsAndClose(writer, text);
    }
    
    public static void copyFile(@NonNull File from, @NonNull File to) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(from));
        try {
            OutputStream out = new BufferedOutputStream(new FileOutputStream(to));
            try {
                IoUtils.copyAllBytes(in, out);
            } finally {
                IoUtils.safeClose(out);
            }
        } finally {
            IoUtils.safeClose(in);
        }
    }
    
    public static void copyFile(@NonNull String fromFilename, @NonNull String toFilename) throws IOException {
        copyFile(new File(fromFilename), new File(toFilename));
    }
    
    @NonNull
    public static Object readObject(@NonNull File file) throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(file);
        ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(fileIn));
        try {
            return in.readObject();
        } finally {
            IoUtils.safeClose(in);
        }
    }
    
    public static void writeObject(@NonNull File file, @NonNull Object object) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(fileOut));
        try {
            out.writeObject(object);
            out.flush();
            fileOut.getFD().sync();
        } finally {
            IoUtils.safeClose(out);
        }
    }
    
    
    public static void updateChecksum(@NonNull File file, @NonNull Checksum checksum) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            IoUtils.updateChecksum(in, checksum);
        } finally {
            IoUtils.safeClose(in);
        }
    }
}
