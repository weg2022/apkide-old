package com.apkide.ui.services.project;

import androidx.annotation.NonNull;

public interface ProjectServiceListener {


	void projectOpened(@NonNull String rootPath);

	void projectClosed(@NonNull String rootPath);

}
