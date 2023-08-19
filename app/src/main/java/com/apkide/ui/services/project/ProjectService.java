package com.apkide.ui.services.project;

import static java.util.Objects.requireNonNull;

import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.apkide.common.AppLog;
import com.apkide.ui.App;
import com.apkide.ui.services.IService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class ProjectService implements IService {
	private final HashMap<String, ProjectManager> myProjectManagerMap = new HashMap<>();
	private ProjectManager myProjectManager;
	private SharedPreferences myPreferences;
	private final List<ProjectServiceListener> myListeners = new Vector<>();

	@Override
	public void initialize() {
		addProjectManager(new ProjectManagerImpl());
		reloadProject();
	}

	@Override
	public void shutdown() {
		if (isProjectOpened()) {
			String rootPath = myProjectManager.getRootPath();
			closeProject();
			getPreferences().edit().putString("open.project", rootPath).apply();
		}
		myProjectManagerMap.clear();
		myListeners.clear();
	}

	public void reloadProject() {
		String rootPath = getPreferences().getString("open.project", "");
		openProject(rootPath);
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


	public boolean checkIsSupportedProjectRootPath(@NonNull String rootPath) {
		if (isProjectOpened()) {
			if (myProjectManager.checkIsSupportedProjectRootPath(rootPath))
				return false;
		}
		for (ProjectManager manager : myProjectManagerMap.values()) {
			if (manager.checkIsSupportedProjectRootPath(rootPath)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkIsSupportedProjectPath(@NonNull String path) {
		if (isProjectOpened()) {
			if (myProjectManager.checkIsSupportedProjectPath(path)) {
				return false;
			}
		}

		for (ProjectManager manager : myProjectManagerMap.values()) {
			if (manager.checkIsSupportedProjectPath(path)) {
				return true;
			}
		}
		return false;
	}


	public boolean openProject(@NonNull String rootPath) {
		AppLog.s(this, "openProject: " + rootPath);
		for (ProjectManager projectManager : myProjectManagerMap.values()) {
			if (projectManager.checkIsSupportedProjectRootPath(rootPath) ||
					projectManager.checkIsSupportedProjectPath(rootPath)) {

				if (isProjectOpened())
					closeProject();

				myProjectManager = projectManager;
				try {
					myProjectManager.open(rootPath);
					getPreferences().edit().putString("open.project", myProjectManager.getRootPath()).apply();
					for (ProjectServiceListener listener : myListeners) {
						listener.projectOpened(requireNonNull(myProjectManager.getRootPath()));
					}
					return true;
				} catch (IOException e) {
					Toast.makeText(App.getUI(), e.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
		}
		return false;
	}

	public void closeProject() {
		if (isProjectOpened()) {
			String rootPath = myProjectManager.getRootPath();
			myProjectManager.close();
			getPreferences().edit().putString("open.project", null).apply();
			for (ProjectServiceListener listener : myListeners) {
				listener.projectClosed(requireNonNull(rootPath));
			}
			App.getFileBrowserService().sync();
		}
	}

	public void syncProject() {
		if (isProjectOpened()) {
			myProjectManager.sync();
		}
	}

	public boolean isProjectOpened() {
		if (myProjectManager != null && myProjectManager.isOpen()) {
			String rootPath = myProjectManager.getRootPath();
			if (rootPath == null) return false;
			File file = new File(rootPath);
			return file.exists() && file.isDirectory();
		}
		return false;
	}
	
	public String getProjectRootPath(){
		if (isProjectOpened()){
			return myProjectManager.getRootPath();
		}
		return "";
	}

	@NonNull
	public String[] getSupportedLanguages() {
		if (isProjectOpened()) {
			return myProjectManager.getSupportedLanguages();
		}
		return new String[0];
	}

	public boolean isProjectFile(@NonNull String filePath) {
		if (isProjectOpened()) {
			return myProjectManager.isProjectFilePath(filePath);
		}
		return false;
	}

	private SharedPreferences getPreferences() {
		if (myPreferences == null)
			myPreferences = App.getPreferences("ProjectService");
		return myPreferences;
	}
}
