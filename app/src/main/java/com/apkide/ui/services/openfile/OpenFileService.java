package com.apkide.ui.services.openfile;

import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.ui.App;
import com.apkide.ui.util.IDEService;

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

	private SharedPreferences getPreferences() {
		if (myPreferences == null)
			myPreferences = App.getPreferences("OpenFileService");
		return myPreferences;
	}

	@Override
	public void initialize() {

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
		if (!isOpenFile(filePath)) {
			return;
		}
		for (OpenFileModelFactory factory : myFactors.values()) {
			if (factory.isSupportedFile(filePath)) {
				try {
					OpenFileModel fileModel = factory.createFileModel(filePath);
					myOpenFileModels.put(filePath, fileModel);
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

	public void closeFile(@NonNull String filePath) {
		if (isOpenFile(filePath)) {
			OpenFileModel model = myOpenFileModels.get(filePath);

			try {
				assert model != null;
				model.save();
			} catch (IOException e) {
				Toast.makeText(App.getUI(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}

			myOpenFileModels.remove(filePath);
			for (OpenFileServiceListener listener : myListeners) {
				listener.fileClosed(filePath, model);
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
	public OpenFileModel getOpenFile(@NonNull String filePath) {
		if (myOpenFileModels.containsKey(filePath)) {
			return myOpenFileModels.get(filePath);
		}
		return null;
	}
}
