package com.apkide.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class MainUIViewModel extends AndroidViewModel {

	public MainUIViewModel(@NonNull Application application) {
		super(application);
	}

	@Override
	protected void onCleared() {
		super.onCleared();
	}
}
