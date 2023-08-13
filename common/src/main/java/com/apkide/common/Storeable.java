package com.apkide.common;

import androidx.annotation.NonNull;

import java.io.IOException;

public interface Storeable {
	void store(@NonNull StoreOutputStream out) throws IOException;

	void restore(@NonNull StoreInputStream in) throws IOException;
}
