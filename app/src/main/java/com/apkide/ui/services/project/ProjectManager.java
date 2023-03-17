package com.apkide.ui.services.project;

import static com.apkide.common.FileUtils.getFileName;
import static java.io.File.separator;

import com.apkide.common.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import brut.util.AssetsProvider;

public final class ProjectManager {

	public static SolutionProject createSDKSolutionProject() {
		File androidJar = AssetsProvider.get().foundFile("android.jar");
		List<SolutionFile> files = new ArrayList<>();
		String rootPath = androidJar.getAbsolutePath();
		files.add(new SolutionFile(rootPath, "Java Binary", true));
		files.add(new SolutionFile(rootPath, "XML", true));
		return new SolutionProject("Android SDK", rootPath, files);
	}

	public static SolutionProject createSolutionProject(String rootPath) {
		List<SolutionFile> files = new ArrayList<>();
		files.add(new SolutionFile(rootPath, "YAML", false));
		files.add(new SolutionFile(rootPath, "XML", false));
		files.add(new SolutionFile(rootPath + separator + "res", "XML", false));
		files.add(new SolutionFile(rootPath + separator + "original", "XML", false));
		addAllSmaliFiles(rootPath, files);
		return new SolutionProject(FileUtils.getFileName(rootPath), rootPath, files);
	}

	public static ProjectModel createProjectModel(String rootPath) {
		List<SolutionProject> projects = new ArrayList<>();
		projects.add(createSDKSolutionProject());
		projects.add(createSolutionProject(rootPath));
		return new ProjectModel(getFileName(rootPath), rootPath, projects);
	}


	private static void addAllSmaliFiles(String rootPath, List<SolutionFile> files) {
		File file = new File(rootPath);
		File[] list = file.listFiles();
		if (list == null)
			files.add(new SolutionFile(rootPath + separator + "smali", "Smali", false));
		else {
			for (File f : list) {
				String name = f.getName();
				if (f.isDirectory() && (name.equals("smali") || name.startsWith("smali_"))) {
					files.add(new SolutionFile(f.getAbsolutePath(), "Smali", false));
				}
			}
		}
	}
}
