package com.apkide.ui.services.openfile;

import android.content.SharedPreferences;

import com.apkide.ui.App;
import com.apkide.ui.util.IDEService;

public class OpenFileService implements IDEService {

	private SharedPreferences myPreferences;




	private SharedPreferences getPreferences(){
		if (myPreferences==null)
			myPreferences= App.getPreferences("OpenFileService");
		return myPreferences;
	}

	@Override
	public void initialize() {

	}

	@Override
	public void shutdown() {

	}
}
