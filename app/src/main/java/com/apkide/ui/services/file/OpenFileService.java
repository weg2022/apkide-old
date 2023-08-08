package com.apkide.ui.services.file;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.apkide.common.FileHighlights;
import com.apkide.ui.App;

import java.util.LinkedHashMap;
import java.util.Map;

public class OpenFileService {

	public interface OpenFileEditor {

		OpenFileModel openFile(String filePath);

		void openEditor(String filePath);

		void closeFile(String filePath);

		String getVisibleFile();

		OpenFileModel getFile(String filePath);
	}

	public interface OpenFileModel {

		void highlighting(FileHighlights highlights);

		void semanticHighlighting(FileHighlights highlights);

		String getFilePath();

		long getLastModified();

		void close();
	}

	private OpenFileEditor myFileEditor;


	private final Map<String, OpenFileModel> myOpenFileModelMap = new LinkedHashMap<>();
	private SharedPreferences myPreferences;

	public void init() {

	}

	public void setFileEditor(OpenFileEditor fileEditor) {
		myFileEditor = fileEditor;
	}

	public void openFile(String filePath, boolean openFileEditor) {
		if (filePath.equals(getFilePath())) {
			return;
		}
		getOpenFileModel(filePath);

		if (openFileEditor)
			myFileEditor.openEditor(filePath);
	}


	public OpenFileModel getOpenFileModel(String filePath) {
		OpenFileModel model = myOpenFileModelMap.get(filePath);
		if (model != null) {
			return model;
		}
		model = myFileEditor.openFile(filePath);
		myOpenFileModelMap.put(filePath, model);
		return model;
	}

	public OpenFileModel getFileModel(String filePath){
		return myOpenFileModelMap.get(filePath);
	}

	public String getFilePath() {
		return "";
	}

	public boolean isOpenFile(String filePath) {
		return myOpenFileModelMap.containsKey(filePath);
	}

	public void closeFile(@NonNull String filePath) {
		if (myOpenFileModelMap.containsKey(filePath)) {
			myOpenFileModelMap.get(filePath).close();
			myOpenFileModelMap.remove(filePath);
			myFileEditor.closeFile(filePath);
		}
	}

	private void update() {
		getPreferences().edit().putStringSet("open.files", myOpenFileModelMap.keySet()).apply();
	}

	private SharedPreferences getPreferences() {
		if (myPreferences == null)
			myPreferences = App.getPreferences("OpenFileService");
		return myPreferences;
	}
}
