package com.apkide.openapi.ls.callback;

import androidx.annotation.NonNull;

import com.apkide.openapi.ls.util.Location;
import com.apkide.openapi.ls.util.Position;

public interface UsageSearcherCallback {
	void usageListStarted(@NonNull String filePath, @NonNull Position position, boolean includeDeclaration);

	void usageListEntityFound(@NonNull Location location);

	void usageListCompleted(@NonNull String filePath, @NonNull Position position, boolean includeDeclaration);
}
