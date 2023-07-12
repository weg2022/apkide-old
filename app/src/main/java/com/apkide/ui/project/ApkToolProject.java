package com.apkide.ui.project;

import static com.apkide.ui.FileSystem.exists;
import static java.io.File.separator;

import com.apkide.common.FileUtils;
import com.apkide.engine.EngineSolution;
import com.apkide.engine.service.CodeModels;
import com.apkide.ui.FileSystem;

import java.util.ArrayList;
import java.util.List;

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

    public static EngineSolution emptySolution() {
        return new EngineSolution(new ArrayList<>(), null, CodeModels.getFilePatternMap());
    }

    public static EngineSolution createSolution(String rootPath, String androidJarPath) {
        ArrayList<EngineSolution.Project> projects = new ArrayList<>();
        projects.add(createProject(rootPath));
        projects.add(createAndroidSDKProject(androidJarPath));
        return new EngineSolution(projects, null, CodeModels.getFilePatternMap());
    }

    private static EngineSolution.Project createProject(String rootPath) {
        String projectName = FileUtils.getFileName(rootPath);
        String buildPath = "";
        ArrayList<EngineSolution.File> files = new ArrayList<>();
        List<String> list = FileSystem.getChildEntries(rootPath);
        for (String path : list) {
            if (FileSystem.isNormalDirectory(path)) {
                String fileName = FileSystem.getName(path);
                if (fileName.equalsIgnoreCase("smali")) {
                    files.add(new EngineSolution.File(path, "Smali", null, false, false));
                } else if (fileName.startsWith("smali_")) {
                    //TODO:需要改进判断 smali 源文件目录
                    files.add(new EngineSolution.File(path, "Smali", null, false, false));
                }
            }
        }
        files.add(new EngineSolution.File(rootPath + separator + "src", "Java", null, false, false));
        files.add(new EngineSolution.File(rootPath + separator + "res", "XML", null, false, false));
        //files.add(new EngineSolution.File(getAndroidManifestXml(rootPath), "XML", null, true, true));
        ArrayList<String> assembles = new ArrayList<>();
        assembles.add("android.jar");
        assembles.add(projectName);
        return new EngineSolution.Project(projectName, rootPath, projectName,
                files, assembles, true,
                "", buildPath, "1.8", false, false,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    private static EngineSolution.Project createAndroidSDKProject(String androidJarPath) {
        ArrayList<EngineSolution.File> files = new ArrayList<>();
        files.add(new EngineSolution.File(androidJarPath, "Java Binary", "", false, true));
        ArrayList<String> assembles = new ArrayList<>();
        assembles.add("android.jar");
        return new EngineSolution.Project("android.jar",
                androidJarPath, "android.jar",
                files, assembles, false,
                "", "", "",
                false, false,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
}
