package com.apkide.ui.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.common.FileSystem;
import com.apkide.common.IoUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JarFileArchiveReader implements FileSystem.FileArchiveReader {
    private final Map<String, CacheEntity> myCaches = new HashMap<>();
    private final JavaBinaryReader myJavaBinaryReader = new JavaBinaryReader();
    
    @NonNull
    @Override
    public String[] getSupportArchiveFilePatterns() {
        return new String[]{"*.jar"};
    }
    
    @NonNull
    @Override
    public Reader getArchiveEntryReader(@NonNull String archivePath, @NonNull String entryName, @Nullable String encoding) throws IOException {
        if (new File(archivePath).isDirectory()) {
            if (entryName.endsWith(".class")) {
                return new InputStreamReader(myJavaBinaryReader.getFileReader(
                                archivePath + File.separator + entryName, encoding));
            }
            FileInputStream inputStream = new FileInputStream(archivePath + File.separator + entryName);
            return encoding == null ?
                    new InputStreamReader(inputStream) :
                    new InputStreamReader(inputStream, encoding);
        } else {
            if (entryName.endsWith(".class")) {
                openFile(archivePath);
                ZipFile archiveFile = getZipFile(archivePath);
                
                if (archiveFile == null) {
                    throw new IOException(archivePath + " is not exists..");
                }
                
                return new InputStreamReader(
                        myJavaBinaryReader.getArchiveFileReader(archivePath, entryName, encoding));
            }
        }
        openFile(archivePath);
        ZipFile archiveFile = getZipFile(archivePath);
        
        if (archiveFile == null) {
            throw new IOException(archivePath + " is not exists..");
        }
        
        ZipEntry entry = getEntry(archivePath,entryName);
        if (entry == null) {
            throw new IOException(archivePath + ":" + entryName + " is not exists..");
        }
        byte[] bytes = IoUtils.readAllBytes(archiveFile.getInputStream(entry));
        return encoding == null ?
                new InputStreamReader(new ByteArrayInputStream(bytes)) :
                new InputStreamReader(new ByteArrayInputStream(bytes), encoding);
        
    }
    
    @NonNull
    @Override
    public List<String> getArchiveDirectoryEntries(@NonNull String archivePath, @NonNull String entryName) throws IOException {
        openFile(archivePath);
        ArrayList<String> entities = new ArrayList<>();
        ZipFile zipFile = getZipFile(archivePath);
        
        if (zipFile == null) {
            return entities;
        }
        
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry next = entries.nextElement();
            String name = next.getName();
            if (name.endsWith(File.separator))
                name = name.substring(0, name.length() - 1);
            if (name.startsWith(entryName) && !name.equals(entryName) &&
                    name.indexOf(File.separator, entryName.length() + 1) == -1) {
                
                if (name.endsWith(".class")) {
                    if (name.lastIndexOf("$") == -1) {
                        entities.add(archivePath + File.separator + name);
                    }
                } else {
                    if (name.startsWith("src/") || name.startsWith("src\\"))
                        name = name.substring(4);
                    entities.add(archivePath + File.separator + name);
                }
            }
        }
        return entities;
    }
    
    @Override
    public boolean isArchiveFileEntry(@NonNull String archivePath, @NonNull String entryName) {
        try {
            openFile(archivePath);
            ZipEntry entry = getEntry(archivePath, entryName);
            if (entry != null) {
                return !entry.isDirectory();
            }
        } catch (IOException ignored) {
        
        }
        return false;
    }
    
    @Override
    public boolean isArchiveDirectoryEntry(@NonNull String archivePath, @NonNull String entryName) {
        try {
            openFile(archivePath);
            if (!entryName.endsWith(File.separator))
                entryName = entryName + File.separator;
            ZipEntry entry = getEntry(archivePath, entryName);
            if (entry != null) {
                return entry.isDirectory();
            }
        } catch (IOException ignored) {
        
        }
        return false;
    }
    
    @Override
    public long getArchiveVersion(@NonNull String archivePath) {
        try {
            openFile(archivePath);
            File file = new File(archivePath);
            if (file.exists()) {
                return file.lastModified();
            }
        } catch (IOException ignored) {
        
        }
        
        return -1;
    }
    
    @Override
    public InputStream getStream(@NonNull String archivePath, @NonNull String entryName) throws IOException {
        if (new File(archivePath).isDirectory()) {
            if (entryName.endsWith(".class")) {
                return myJavaBinaryReader.getFileReader(
                        archivePath + File.separator + entryName, null);
            }
            return new FileInputStream(archivePath + File.separator + entryName);
        } else {
            if (entryName.endsWith(".class")) {
                openFile(archivePath);
                ZipFile archiveFile = getZipFile(archivePath);
            
                if (archiveFile == null) {
                    throw new IOException(archivePath + " is not exists..");
                }
            
                return
                        myJavaBinaryReader.getArchiveFileReader(archivePath, entryName, null);
            }
        }
        openFile(archivePath);
        ZipFile archiveFile = getZipFile(archivePath);
    
        if (archiveFile == null) {
            throw new IOException(archivePath + " is not exists..");
        }
    
        ZipEntry entry = getEntry(archivePath,entryName);
        if (entry == null) {
            throw new IOException(archivePath + ":" + entryName + " is not exists..");
        }
        byte[] bytes = IoUtils.readAllBytes(archiveFile.getInputStream(entry));
        return new ByteArrayInputStream(bytes);
    }
    
    @Override
    public long getLastModified(@NonNull String archivePath, @NonNull String entryName) {
        try {
            openFile(archivePath);
            ZipEntry entry = getEntry(archivePath, entryName);
            if (entry != null)
                return entry.getLastModifiedTime().toMillis();
        } catch (IOException ignored) {
        
        }
        return -1;
    }
    
    @Override
    public long getSize(@NonNull String archivePath, @NonNull String entryName) {
        try {
            openFile(archivePath);
            ZipEntry entry = getEntry(archivePath, entryName);
            if (entry != null)
                return entry.getSize();
        } catch (IOException ignored) {
        
        }
        return -1;
    }
    
    @Override
    public boolean isOpenedArchive(@NonNull String archivePath) {
        if (myCaches.containsKey(archivePath)) {
            CacheEntity entity = myCaches.get(archivePath);
            if (entity != null && entity.file != null) {
                File file = new File(archivePath);
                return entity.lastModified == file.lastModified();
            }
        }
        return false;
    }
    
    @Override
    public void close(@NonNull String archivePath) throws IOException {
        if (myCaches.containsKey(archivePath)) {
            CacheEntity entity = myCaches.get(archivePath);
            if (entity != null && entity.file != null) {
                entity.file.close();
            }
        }
        myCaches.remove(archivePath);
        myJavaBinaryReader.closeArchive(archivePath);
    }
    
    @Nullable
    private ZipFile getZipFile(String archivePath) {
        if (myCaches.containsKey(archivePath)) {
            CacheEntity entity = myCaches.get(archivePath);
            if (entity != null) {
                return entity.file;
            }
        }
        return null;
    }
    
    
    @Nullable
    private ZipEntry getEntry(String archivePath, String entryName) {
        ZipFile zipFile = getZipFile(archivePath);
        if (zipFile == null) return null;
        ZipEntry entry = zipFile.getEntry(entryName);
        if (entry == null) entry = zipFile.getEntry("src/" + entryName);
        if (entry == null) entry = zipFile.getEntry("src\\" + entryName);
        return entry;
    }
    
    
    private void clearCaches() throws IOException {
        for (String filePath : myCaches.keySet()) {
            CacheEntity entity = myCaches.get(filePath);
            File file = new File(filePath);
            if (entity != null && file.lastModified() != entity.lastModified) {
                if (entity.file != null)
                    entity.file.close();
                myCaches.remove(filePath);
                myJavaBinaryReader.closeArchive(filePath);
            }
        }
    }
    
    private void openFile(String filePath) throws IOException {
        clearCaches();
        if (myCaches.containsKey(filePath)) {
            CacheEntity entity = myCaches.get(filePath);
            if (entity != null && entity.file != null) {
                File file = new File(filePath);
                if (entity.lastModified == file.lastModified()) {
                    return;
                }
            }
        }
        ZipFile zipFile = new ZipFile(filePath);
        CacheEntity entity = new CacheEntity();
        entity.file = zipFile;
        entity.lastModified = new File(filePath).lastModified();
        myCaches.put(filePath, entity);
    }
    
    
    @Override
    public void close() throws IOException {
        for (CacheEntity entity : myCaches.values()) {
            if (entity != null && entity.file != null)
                entity.file.close();
        }
        myCaches.clear();
        myJavaBinaryReader.close();
    }
    
    private static class CacheEntity {
        public ZipFile file;
        public long lastModified;
    }
}