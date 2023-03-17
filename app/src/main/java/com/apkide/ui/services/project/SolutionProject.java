package com.apkide.ui.services.project;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SolutionProject {
	private final String name;
	private final String rootPath;
	private final List<SolutionFile> files;

	public SolutionProject(String name, String rootPath, List<SolutionFile> files) {
		this.name = name;
		this.rootPath = rootPath;
		this.files = files;
	}

	@NonNull
	public String getName() {
		return name;
	}

	@NonNull
	public String getRootPath() {
		return rootPath;
	}

	@NonNull
	public List<SolutionFile> getFiles() {
		return files;
	}

	public void setFiles(List<SolutionFile> files) {
		this.files.clear();
		this.files.addAll(files);
	}

	public void addFile(SolutionFile file) {
		for (SolutionFile solutionFile : files) {
			if (solutionFile.path.equals(file.path) && solutionFile.type.equals(file.type)) {
				files.remove(solutionFile);
			}
		}
		files.add(file);
	}

	public List<SolutionFile> getFilesOfPath(String path) {
		List<SolutionFile> list = new ArrayList<>();
		for (SolutionFile file : files) {
			if (file.path.equals(path)) {
				list.add(file);
			}
		}
		return list;
	}

	public void removeFile(String path, String type) {
		for (SolutionFile file : files) {
			if (file.path.equals(path) && file.type.equals(type)) {
				files.remove(file);
			}
		}
	}

	public void removeAllFileOfPath(String path) {
		for (SolutionFile file : files) {
			if (file.path.equals(path)) {
				files.remove(file);
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SolutionProject that = (SolutionProject) o;
		return name.equals(that.name) && rootPath.equals(that.rootPath) && files.equals(that.files);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, rootPath, files);
	}
}
