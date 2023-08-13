package com.apkide.common;

import androidx.annotation.NonNull;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class StoreOutputStream extends DataOutputStream {
	public StoreOutputStream(@NonNull String filePath,@NonNull Deflater deflater) throws IOException {
		super(new BufferedOutputStream(new DeflaterOutputStream(new FileOutputStream(filePath), deflater)));
	}

}
