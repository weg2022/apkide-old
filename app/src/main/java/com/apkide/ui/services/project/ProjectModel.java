package com.apkide.ui.services.project;

import static java.io.File.separator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProjectModel {
	private String projectName;
	private String rootPath;
	private List<SolutionProject> solutionProjects;

	public ProjectModel(String projectName, String rootPath, List<SolutionProject> solutionProjects) {
		this.projectName = projectName;
		this.rootPath = rootPath;
		this.solutionProjects = solutionProjects;
	}

	public String getProjectName() {
		return projectName;
	}

	public List<SolutionProject> getSolutionProjects() {
		return solutionProjects;
	}

	public void updateSolutionProject(SolutionProject project) {
		for (SolutionProject solutionProject : solutionProjects) {
			if (solutionProject.getRootPath().equals(project.getRootPath()))
				solutionProjects.remove(solutionProject);
		}
		solutionProjects.add(project);
	}

	public void removeSolutionProject(String rootPath) {
		for (SolutionProject project : solutionProjects) {
			if (project.getRootPath().equals(rootPath))
				solutionProjects.remove(project);
		}
	}

	public void removeSolutionProject(SolutionProject project) {
		solutionProjects.remove(project);
	}

	public String getOriginalAndroidManifestPath() {
		return getOriginalPath() + separator + "AndroidManifest.xml";
	}

	public String getAndroidManifestPath() {
		return rootPath + separator + "AndroidManifest.xml";
	}

	public String getApkToolConfigPath() {
		return rootPath + separator + "apktool.yml";
	}

	public String getRootPath() {
		return rootPath;
	}

	public String getBuildPath() {
		return rootPath + separator + "build";
	}

	public String getAssetsPath() {
		return rootPath + separator + "assets";
	}

	public String getLibPath() {
		return rootPath + separator + "lib";
	}

	public String getResPath() {
		return rootPath + separator + "res";
	}


	public String getOriginalMETAINFPath() {
		return getOriginalPath() + separator + "META-INF";
	}

	public String getMETAINFPath() {
		return rootPath + separator + "META-INF";
	}

	public String getUnknownPath() {
		return rootPath + separator + "unknown";
	}

	public String getOriginalPath() {
		return rootPath + separator + "original";
	}

	public List<String> getSmaliPaths() {
		ArrayList<String> paths = new ArrayList<>();
		File root = new File(rootPath);
		File[] files = root.listFiles();
		if (files != null) {
			for (File file : files) {
				String name = file.getName();
				if (file.isDirectory() && name.equals("smali") || name.startsWith("smali_")) {
					paths.add(file.getAbsolutePath());
				}
			}
		}
		return paths;
	}


}
