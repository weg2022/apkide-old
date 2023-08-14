package com.apkide.ui.services.project;

import static com.apkide.common.FileSystem.getEnclosingParent;
import static java.io.File.separator;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.common.AppLog;
import com.apkide.common.FileSystem;
import com.apkide.ui.App;
import com.apkide.ui.R;

import java.io.File;
import java.io.IOException;

public class IDEProjectManager implements ProjectManager {

	private String myRootPath;
	private IDEConfigureFile myConfigureFile;

	@NonNull
	@Override
	public String getName() {
		return "Dev-A";
	}

	@NonNull
	@Override
	public String[] getSupportedLanguages() {
		return new String[]{"Smali", "Java", "XML"};
	}

	@Override
	public boolean checkIsSupportedProjectRootPath(@NonNull String rootPath) {
		File config = new File(rootPath, FileSystem.getName(rootPath) + ".dev");
		AppLog.s(this, config.getAbsolutePath());
		File apktool = new File(rootPath, "apktool.yml");
		return config.exists() || apktool.exists();
	}

	@Override
	public boolean checkIsSupportedProjectPath(@NonNull String path) {
		String workspace = getEnclosingParent(path, s -> {
			File config = new File(s, FileSystem.getName(s) + ".dev");
			File apktool = new File(s, "apktool.yml");
			return config.exists() || apktool.exists();
		});
		return workspace != null;
	}

	@Override
	public boolean isProjectFilePath(@NonNull String path) {
		if (isOpen()) {
			return getEnclosingParent(path, s -> s.equals(myRootPath)) != null;
		}
		return false;
	}

	@Override
	public void open(@NonNull String rootPath) throws IOException {
		String workspace;
		if (checkIsSupportedProjectRootPath(rootPath)) {
			workspace = rootPath;
		} else {
			workspace = getEnclosingParent(rootPath, s -> {
				File config = new File(s, FileSystem.getName(s) + ".dev");
				File apktool = new File(s, "apktool.yml");
				return config.exists() || apktool.exists();
			});
		}
		if (workspace == null) {
			throw new IOException("rootPath is null");
		}
		myRootPath = workspace;
		myConfigureFile = new IDEConfigureFile(
				myRootPath + separator + FileSystem.getName(myRootPath) + ".dev",
				myRootPath + separator + "apktool.yml");
	}

	@Override
	public void close() {

		myRootPath = null;
		myConfigureFile.destroy();
	}

	@Override
	public void sync() {
		try {
			myConfigureFile.sync();
		} catch (ConfigureFileException e) {
			Toast.makeText(App.getUI(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean isOpen() {
		return myRootPath != null;
	}

	@Nullable
	@Override
	public String getRootPath() {
		return myRootPath;
	}


	@Nullable
	@Override
	public String resolvePath(@NonNull String path) {
		if (!isOpen()) return null;
		return FileSystem.resolvePath(myRootPath, path);
	}

	@Override
	public int getIcon() {
		return R.drawable.project_properties;
	}
}
