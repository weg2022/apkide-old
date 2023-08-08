package com.apkide.common.store;

import androidx.annotation.NonNull;

import java.io.IOException;

public interface Storeable {
	void store(@NonNull StoreOutputStream out) throws IOException;

	void restore(@NonNull StoreInputStream in) throws IOException;
}
