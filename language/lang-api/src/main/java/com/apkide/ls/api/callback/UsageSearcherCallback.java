package com.apkide.ls.api.callback;

import androidx.annotation.NonNull;

import com.apkide.ls.api.util.Location;
import com.apkide.ls.api.util.Position;

public interface UsageSearcherCallback {
	void usageListStarted(@NonNull String filePath, @NonNull Position position, boolean includeDeclaration);

	void usageListEntityFound(@NonNull Location location);

	void usageListCompleted(@NonNull String filePath, @NonNull Position position, boolean includeDeclaration);
}
