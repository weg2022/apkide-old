package com.apkide.codemodel.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.Reader;

public interface BinaryArchiveSupport {
	
	@NonNull
	String[] getSupportedFileExtensions();
	
	@NonNull
	default Reader getSignatureFileContent(@NonNull String archivePath,@NonNull String entryName) throws IOException {
		return getSignatureFileContent(archivePath,entryName,"UTF-8");
	}
	
	@NonNull
	Reader getSignatureFileContent(@NonNull String archivePath,@NonNull String entryName,@NonNull String encoding) throws IOException;
	
	
	@Nullable
	String[] getSignatureFiles(String archivePath)throws IOException;
	
}
