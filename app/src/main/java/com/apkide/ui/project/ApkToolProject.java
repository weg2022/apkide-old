package com.apkide.ui.project;

import static com.apkide.ui.FileSystem.exists;
import static java.io.File.separator;


public class ApkToolProject implements ConfigurationMarker {

    public static boolean isProjectRoot(String filePath) {
        return exists(getApkToolYml(filePath)) && exists(getAndroidManifestXml(filePath));
    }

    public static String getApkToolYml(String rootPath) {
        return rootPath + separator + "apktool.yml";
    }

    public static String getAndroidManifestXml(String rootPath) {
        return rootPath + separator + "AndroidManifest.xml";
    }

    public static String getResourcesArsc(String rootPath) {
        return rootPath + separator + "resources.arsc";
    }

    public static String getResDir(String rootPath) {
        return rootPath + separator + "res";
    }

    public static String getAssetsDir(String rootPath) {
        return rootPath + separator + "assets";
    }

    public static String getOriginalDir(String rootPath) {
        return rootPath + separator + "original";
    }

    public static String getSmaliDir(String rootPath) {
        return rootPath + separator + "smali";
    }

}
