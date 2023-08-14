package com.apkide.ui.services.project;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.apkide.common.FileSystem;
import com.apkide.ui.App;
import com.apkide.ui.util.IDEService;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class ProjectService implements IDEService {


	private final HashMap<String, ProjectManager> myProjectManagerMap = new HashMap<>();
	private ProjectManager myProjectManager;
	private String myProjectRootPath;
	private SharedPreferences myPreferences;
	private final List<ProjectServiceListener> myListeners = new Vector<>();

	@Override
	public void initialize() {
		addProjectManager(new TestProjectManager());
	}

	@Override
	public void shutdown() {
		getPreferences().edit().putString("open.project", myProjectRootPath).apply();
		closeProject();
		myProjectManagerMap.clear();
		myProjectRootPath = null;
		myPreferences = null;
		myListeners.clear();
	}

	public void reloadProject() {
		String rootPath = getPreferences().getString("open.project", "");
		if (isSupportedProject(rootPath)) {
			openProject(rootPath);
		}
	}

	public void addListener(@NonNull ProjectServiceListener listener) {
		if (!myListeners.contains(listener))
			myListeners.add(listener);
	}

	public void removeListener(@NonNull ProjectServiceListener listener) {
		myListeners.remove(listener);
	}

	public void addProjectManager(@NonNull ProjectManager manager) {
		myProjectManagerMap.put(manager.getName(), manager);
	}

	public void removeProjectManger(@NonNull String name) {
		myProjectManagerMap.remove(name);
	}

	public List<String> getProjectManagers() {
		return new ArrayList<>(myProjectManagerMap.keySet());
	}

	public boolean isSupportedProject(@NonNull String rootPath) {
		if (isProjectOpened()) {
			if (rootPath.equals(myProjectRootPath)) {
				return false;
			}
		}

		for (ProjectManager value : myProjectManagerMap.values()) {
			if (value.isSupportedProject(rootPath) || value.isProjectFile(rootPath)) {
				return true;
			}
		}
		return false;
	}


	public boolean openProject(@NonNull String rootPath) {
		if (FileSystem.isNormalDirectory(rootPath)) {
			if (isProjectOpened()) {
				closeProject();
			}

			for (ProjectManager projectManager : myProjectManagerMap.values()) {
				if (projectManager.isSupportedProject(rootPath)) {
					projectManager.open(rootPath);
					myProjectManager = projectManager;
					myProjectRootPath = rootPath;
					getPreferences().edit().putString("open.project", rootPath).apply();
					return true;
				}
			}
		}
		return false;
	}

	public void closeProject() {
		if (isProjectOpened()) {
			myProjectManager.close();
			myProjectRootPath = null;
			getPreferences().edit().putString("open.project", null).apply();
		}
	}

	public void syncProject() {
		if (isProjectOpened()) {
			myProjectManager.sync();
		}
	}

	public boolean isProjectOpened() {
		if (myProjectManager != null && myProjectRootPath != null) {
			File file = new File(myProjectRootPath);
			return file.exists() && file.isDirectory();
		}
		return false;
	}

	public boolean isProjectFile(@NonNull String filePath) {
		if (isProjectOpened()) {
			return FileSystem.getEnclosingParent(filePath, myProjectRootPath) != null;
		} else {
			for (ProjectManager projectManager : myProjectManagerMap.values()) {
				if (projectManager.isProjectFile(filePath)) {
					return true;
				}
			}
		}
		return false;
	}

	private SharedPreferences getPreferences() {
		if (myPreferences == null)
			myPreferences = App.getPreferences("ProjectService");
		return myPreferences;
	}
}
