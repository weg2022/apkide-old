package com.apkide.common.store;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class StoreInputStream extends DataInputStream {
	public StoreInputStream(@NonNull String filePath, @NonNull Inflater inflater) throws IOException {
		super(new BufferedInputStream(new InflaterInputStream(new FileInputStream(filePath), inflater)));
	}

}
