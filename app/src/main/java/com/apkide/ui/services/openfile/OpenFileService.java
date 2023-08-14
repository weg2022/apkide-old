package com.apkide.ui.services.openfile;

import static java.util.Objects.requireNonNull;

import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.ui.App;
import com.apkide.ui.util.IDEService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class OpenFileService implements IDEService {

	private SharedPreferences myPreferences;

	private final HashMap<String, OpenFileModel> myOpenFileModels = new HashMap<>();

	private final HashMap<String, OpenFileModelFactory> myFactors = new HashMap<>();
	private final List<OpenFileServiceListener> myListeners = new Vector<>();

	private String myVisibleFilePath;

	private SharedPreferences getPreferences() {
		if (myPreferences == null)
			myPreferences = App.getPreferences("OpenFileService");
		return myPreferences;
	}

	@Override
	public void initialize() {
		addOpenFileModelFactory(new DefaultOpenFileModelFactory());
	}

	@Override
	public void shutdown() {

	}

	public void addListener(@NonNull OpenFileServiceListener listener) {
		if (!myListeners.contains(listener))
			myListeners.add(listener);
	}

	public void removeListener(@NonNull OpenFileServiceListener listener) {
		myListeners.remove(listener);
	}

	public void addOpenFileModelFactory(@NonNull OpenFileModelFactory factory) {
		myFactors.put(factory.getName(), factory);
	}

	public void removeOpenFileModelFactory(@NonNull String name) {
		myFactors.remove(name);
	}

	public void openFile(@NonNull String filePath) {
		if (isOpenFile(filePath)) {
			myVisibleFilePath = filePath;
			for (OpenFileServiceListener listener : myListeners) {
				listener.fileOpened(filePath, requireNonNull(getOpenFileModel(filePath)));
			}
			return;
		}
		for (OpenFileModelFactory factory : myFactors.values()) {
			if (factory.isSupportedFile(filePath)) {
				try {
					OpenFileModel fileModel = factory.createFileModel(filePath);
					myOpenFileModels.put(filePath, fileModel);
					myVisibleFilePath = filePath;
					for (OpenFileServiceListener listener : myListeners) {
						listener.fileOpened(filePath, fileModel);
					}
				} catch (IOException e) {
					Toast.makeText(App.getUI(), e.getMessage(), Toast.LENGTH_SHORT).show();
				}
				return;
			}
		}
	}

	public void closeAll() {
		for (String filePath : myOpenFileModels.keySet()) {
			closeFile(filePath);
		}
	}

	public void closeVisibleFile() {
		if (myVisibleFilePath != null)
			closeFile(myVisibleFilePath);
	}

	public void closeFile(@NonNull String filePath) {
		if (isOpenFile(filePath)) {
			if (filePath.equals(myVisibleFilePath))
				myVisibleFilePath = null;
			OpenFileModel model = myOpenFileModels.get(filePath);
			myOpenFileModels.remove(filePath);
			if (model != null) {
				saveFile(filePath);
				for (OpenFileServiceListener listener : myListeners) {
					listener.fileClosed(filePath, model);
				}
			}
		}
	}

	public boolean isSupportedFile(@NonNull String filePath) {
		for (OpenFileModelFactory factory : myFactors.values()) {
			if (factory.isSupportedFile(filePath)) {
				return true;
			}
		}
		return false;
	}

	public boolean isOpen() {
		return !myOpenFileModels.isEmpty();
	}

	public String getVisibleFilePath() {
		return myVisibleFilePath;
	}

	public boolean isOpenFile(@NonNull String filePath) {
		if (myOpenFileModels.containsKey(filePath)) {
			//TODO: check file is invalid
			return true;
		}
		return false;
	}

	public List<String> getOpenFilePaths() {
		List<String> paths = new ArrayList<>();
		for (String path : myOpenFileModels.keySet()) {
			//TODO: check file is invalid
			paths.add(path);
		}
		return paths;
	}

	@Nullable
	public OpenFileModel getOpenFileModel(@NonNull String filePath) {
		if (myOpenFileModels.containsKey(filePath)) {
			return myOpenFileModels.get(filePath);
		}
		return null;
	}

	public void saveFile(@NonNull String filePath) {

		OpenFileModel fileModel = myOpenFileModels.get(filePath);
		if (fileModel != null && !fileModel.isReadOnly()) {
			File file = new File(fileModel.getFilePath());
			if (file.lastModified() != file.lastModified()) {
				try {
					fileModel.save();
				} catch (IOException e) {
					e.printStackTrace();
					Toast.makeText(App.getUI(), e.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	public void saveVisibleFile() {
		if (myVisibleFilePath != null)
			saveFile(myVisibleFilePath);
	}

	public void saveAll() {
		for (String filePath : myOpenFileModels.keySet()) {
			saveFile(filePath);
		}
	}
}
