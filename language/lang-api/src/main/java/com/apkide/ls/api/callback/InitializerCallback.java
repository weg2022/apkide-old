package com.apkide.ls.api.callback;

import androidx.annotation.NonNull;

import java.util.Map;

public interface InitializerCallback {

	void initializeStarted();

	void initializeCompleted(@NonNull Map<String, Object> options);

}
