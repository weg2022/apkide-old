package com.apkide.ui.browsers.project;

import android.content.Context;

import androidx.annotation.NonNull;

import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.browsers.HeaderBrowserLayout;
import com.apkide.ui.services.project.ProjectServiceListener;

public class ProjectBrowser extends HeaderBrowserLayout implements ProjectServiceListener {
	public ProjectBrowser(@NonNull Context context) {
		super(context);

		getHeaderIcon().setImageResource(R.drawable.project_properties);
		getHeaderLabel().setText(R.string.browser_label_project);
		getHeaderHelp().setOnClickListener(v -> {

		});

		App.getProjectService().reloadProject();
		App.getProjectService().addListener(this);
	}

	@NonNull
	@Override
	public String getBrowserName() {
		return "ProjectBrowser";
	}

	@Override
	public void projectOpened(@NonNull String rootPath) {

	}

	@Override
	public void projectClosed(@NonNull String rootPath) {

	}
}
