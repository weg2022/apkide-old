package com.apkide.ui.services.project.apktool;

import androidx.annotation.NonNull;

import com.apkide.common.FileSystem;
import com.apkide.common.FileUtils;

import java.io.File;
import java.io.IOException;

public class ApkToolProjectHelper {
    
    
    @NonNull
    public static File convert(File rootPath, File destRootPath) throws IOException {
        if (!rootPath.exists() || !rootPath.isDirectory()) {
            throw new IOException(rootPath.getAbsolutePath() +
                    " is not exists or file type not supported.");
        }
        
        if (destRootPath.exists())
            FileSystem.delete(destRootPath.getAbsolutePath());
        
        copyFile(rootPath, destRootPath);
        
        File configFile=new File(rootPath,"apktool.yml");
        
        
        if (!destRootPath.exists())
            throw new IOException("Convert error: dest not exists.");
        
        return destRootPath;
    }
    
    private static void copyFile(File filePath, File destPath) throws IOException {
        if (filePath.isFile()) {
            FileUtils.copyFile(filePath, destPath);
        } else {
            File[] vFiles = filePath.listFiles();
            if (vFiles == null) {
                return;
            }
            
            for (File vFile : vFiles) {
                copyFile(vFile, new File(destPath, vFile.getName()));
            }
        }
    }
    
    
}
