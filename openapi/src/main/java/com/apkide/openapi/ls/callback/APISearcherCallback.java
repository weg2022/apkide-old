package com.apkide.openapi.ls.callback;

import androidx.annotation.NonNull;

import com.apkide.openapi.ls.util.Location;
import com.apkide.openapi.ls.util.Position;

public interface APISearcherCallback {
	void apiSearchStarted(@NonNull String filePath, @NonNull Position position);

	void apiListStarted(@NonNull String filePath, @NonNull Position position);

	void apiListEntityFound(@NonNull Location location);

	void apiListCompleted(@NonNull String filePath, @NonNull Position position);

	void apiSearchCompleted(@NonNull String filePath, @NonNull Position position);
}
