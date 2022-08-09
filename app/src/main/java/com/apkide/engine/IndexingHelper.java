package com.apkide.engine;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class IndexingHelper {
    private static final String TAG = "IndexingHelper";

    public static List<String> jarIndexing(JarFile file) {
        ArrayList<String> classes = new ArrayList<>();
        Enumeration<JarEntry> entries = file.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (!entry.isDirectory() && entry.getName().endsWith(".class"))
                classes.add(entry.getName());
            else
                Log.d(TAG, "jarIndexing:  ignore entry "+entry.getName());
        }
        return classes;
    }

    public static List<String> smaliIndexing(File dir) {
        ArrayList<String> list = new ArrayList<>();
        findSmali(dir, list);
        return list;
    }

    private static void findSmali(File file, List<String> list) {
        if (file.isFile() && file.getName().endsWith(".smali"))
            list.add(file.getAbsolutePath());
        else if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    findSmali(child, list);
                }
            }
        } else
            Log.d(TAG, "findSmali: ignore files " + file.getAbsolutePath());
    }
}

