package com.apkide.openapi.ls.callback;

import androidx.annotation.NonNull;

import com.apkide.openapi.ls.Completion;
import com.apkide.openapi.ls.util.Position;

public interface CodeCompleterCallback {

	void completionListStarted(@NonNull String filePath, @NonNull Position position);

	void completionListEntityFound(@NonNull Completion completion);

	void completionListCompleted(@NonNull String filePath, @NonNull Position position);

}
