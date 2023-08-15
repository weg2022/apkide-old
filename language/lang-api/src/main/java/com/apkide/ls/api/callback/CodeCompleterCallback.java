package com.apkide.ls.api.callback;

import androidx.annotation.NonNull;

import com.apkide.ls.api.Completion;
import com.apkide.ls.api.util.Position;

public interface CodeCompleterCallback {

	void completionListStarted(@NonNull String filePath, @NonNull Position position);

	void completionListEntityFound(@NonNull Completion completion);

	void completionListCompleted(@NonNull String filePath, @NonNull Position position);

}
